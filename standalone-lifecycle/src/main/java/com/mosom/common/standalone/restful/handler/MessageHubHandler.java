/*
 * ���α׷��� : MessageHubHandler
 * �����衡�� : Thomas Parker(�ӿ���) - (2025.07.09)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2025.07.09)
 * ���������� : �������� �ý��� REST API Service - ���� ������ MessageHub(LGU+)
 * Kakao, RCS, SMS/MMS ���� �߼� �� ARS(ALIMTALK->RCS->SMS/MMS) Fallback
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.data.ImmutableMessageHubTemplate;
import com.mosom.common.standalone.cache.data.MessageHubTemplateCache;
import com.mosom.common.standalone.cache.document.SQLXmlCache;
import com.mosom.common.standalone.restful.ProcessException;
import com.mosom.common.standalone.restful.RequestStructure;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.mosom.common.standalone.cache.data.DataCacheTypeNames.MESSAGEHUBTEMPLATE;
import static com.mosom.common.standalone.cache.data.ImmutableMessageHubTemplate.Activations.DEACTIVATE;
import static com.mosom.common.standalone.cache.data.ImmutableMessageHubTemplate.SendTypes.BATCH;
import static com.mosom.common.standalone.cache.data.ImmutableMessageHubTemplate.SendTypes.REAL;
import static com.mosom.common.standalone.cache.document.DocumentCacheTypeNames.SQLXML;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;

/**
 * URI:/{0}/{1}/{2}/messagehub[/{parameter(n)}/{value(n)}..*]
 * Example:/rpa/json/utf8/messagehub
 *         /type/alimtalk/code/202202210052
 *         /bind1/�ӿ���/bind2/130-00-00-0000000/bind3/0000-00-00
 *         /recipient1/01000000001/recipient2/01000000002
 * Example Message code:202202210052(�����̿�����ȳ�)
 * [BNK��������] #{����}���� #{���¹�ȣ} #{����} �����̿� (�����븮 ������ ���Ӽ����ʿ�)
 * 0:Requester
 * 1:Response Type
 * 2:Response Charset
 * messagehub:Response Provider
 * N:PARAMETER(n), VALUE(n)
 * Requester, Response Type, Response Charset, Response Provider�� ������ ��� Parameter�� POST ��� ��� ����
 */
public class MessageHubHandler extends MobileMessageHandler<MessageHubHandler.MessageSpecification> {

    enum Preconditions {

        /**
         * type : Message Type
         */
        type

    }

    enum MessageTypes {

        /**
         * SMS
         * MMS
         * GDS        : Global SMS
         * RCS
         * ALIMTALK   : Kakao �˸���
         * FRIENDTALK : Kakao ģ����
         * PUSH       : ��Ǫ��
         * UMS        : ���� ���ø�
         */
        SMS, MMS, GDS, RCS, ALIMTALK, FRIENDTALK, PUSH, UMS

    }

    enum FallbackTypes {

        /**
         * ARS(Default) : ALIMTALK->RCS->SMS/MMS
         * RS           : RCS->SMS/MMS
         * NONE         : N/A
         */
        ARS, RS, NONE

    }

    static class MessageSpecification {

        final String messageType;

        final String title;

        final String message;

        final String mobile;

        //�ٸ� Service Domain���� ȣ���� ��� Requesters.{NAME}.channelCode() ���� �Ҵ�
        final String senderChannel;

        String fallbackChannel;

        String templateCode;

        String sender;

        Long reserveDate;

        String br;

        String user;

        String custNumber;

        public MessageSpecification(RequestStructure request, String messageType, String title, String message, String mobile) throws ProcessException {
            this.messageType = messageType;
            this.title = title;
            this.message = message;
            this.mobile = mobile;
            senderChannel = request.getRequester().channelCode();
            fallbackChannel = request.get("fallback");
            templateCode = request.get("code");
            sender = request.get("sender");
            br = request.get("br");
            user = request.get("user");
            custNumber = request.get("custNumber");

            if (request.get("reserveDate") != null) {
                reserveDate = parseReserveDate(request.get("reserveDate"));
            }
        }

    }

    public MessageHubHandler() {
        super();
    }

    @Override
    protected String provider() {
        return getClass().getSimpleName();
    }

    @Override
    protected void validation() throws ProcessException {
        //Message Type Ȯ��
        MessageTypes messageType = validateAndNormalizeMessageType();
        //Template Code, Title, Message Ȯ��
        validateMessageContents(messageType);
        //������ Ȯ��
        validateRecipients();
        //Fallback Ȯ��
        validateAndSetFallback(messageType);
    }

    private MessageTypes validateAndNormalizeMessageType() throws ProcessException {
        String messageType = request.get(Preconditions.type.name());

        if (messageType == null) {
            throw new ProcessException("[MessageType] not specified.");
        }

        try {
            messageType = messageType.toUpperCase();
            MessageTypes result = MessageTypes.valueOf(messageType); // ��ȿ�� �˻�
            request.set(Preconditions.type.name(), messageType);

            return result;
        } catch (IllegalArgumentException e) {
            throw new ProcessException("[MessageType] not specified.(" + e.getMessage() + ")", e);
        }
    }

    private void validateMessageContents(MessageTypes messageType) throws ProcessException {
        if (MessageTypes.ALIMTALK == messageType) {
            if (!request.isParameterContainsKey("code")) {
                throw new ProcessException("[TemplateCode] not specified.");
            }
        } else {
            if (!request.isParameterContainsKey("title")) {
                throw new ProcessException("[Title] not specified.");
            }
            if (!request.isParameterContainsKey("message")) {
                throw new ProcessException("[Message] not specified.");
            }
        }
    }

    private void validateRecipients() throws ProcessException {
        if (!request.isParameterContainsCharacter("recipient")) {
            throw new ProcessException("[RECIPIENT] parameter not specified.");
        }
    }

    private void validateAndSetFallback(MessageTypes messageType) throws ProcessException {
        String fallbackType = request.get("fallback");

        if (fallbackType == null) {
            //fallback ������ �� �⺻�� ����
            setDefaultFallback(messageType);
            return;
        }

        //����ڰ� ������ fallback ��ȿ�� �˻�
        FallbackTypes specifiedFallbackType;

        try {
            specifiedFallbackType = FallbackTypes.valueOf(fallbackType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ProcessException("[FallbackType] not specified.(" + e.getMessage() + ")", e);
        }

        // NONE�� �׻� ���
        if (FallbackTypes.NONE == specifiedFallbackType) {
            request.set("fallback", specifiedFallbackType.name());
            return;
        }

        // �޽��� Ÿ�԰� ȣȯ�� �˻�
        validateFallbackCompatibilityAndSetFallback(messageType, specifiedFallbackType);
    }

    private void setDefaultFallback(MessageTypes messageType) throws ProcessException {
        request.set("fallback", getDefaultFallback(messageType).name());
    }

    private FallbackTypes getDefaultFallback(MessageTypes messageType) {
        switch (messageType) {
            case ALIMTALK:
                return FallbackTypes.ARS;
            case RCS:
                return FallbackTypes.RS;
            default:
                return FallbackTypes.NONE;
        }
    }

    private void validateFallbackCompatibilityAndSetFallback(MessageTypes messageType, FallbackTypes fallbackType) throws ProcessException {
        FallbackTypes finalFallbackType = fallbackType;

        switch (messageType) {
            case ALIMTALK:
                if (fallbackType != FallbackTypes.ARS) {
                    log().info("ALIMTALK only supports ARS fallback, but " + fallbackType + " specified. Setting to ARS.");
                    finalFallbackType = FallbackTypes.ARS;
                }

                break;
            case RCS:
                if (fallbackType != FallbackTypes.RS) {
                    log().info("RCS only supports RS fallback, but " + fallbackType + " specified. Setting to RS.");
                    finalFallbackType = FallbackTypes.RS;
                }

                break;
            case SMS:
            case MMS:
            case GDS:
            case FRIENDTALK:
            case PUSH:
            case UMS:
                //������ ������ fallback �ǹ̰� �����Ƿ� NONE���� ���� ����
                log().info("MessageType[" + messageType + "] does not support fallback. Setting to NONE.");
                finalFallbackType = FallbackTypes.NONE;
                break;
        }

        request.set("fallback", finalFallbackType.name());
    }

    @Override
    protected void setMessageContents() throws ProcessException {
        String messageType = request.get(Preconditions.type.name());
        String title = request.get("title");
        String message = request.get("message");

        //ALIMTALK�� ��� Title, ���ε� Message �Ҵ�
        if (messageType.equals(MessageTypes.ALIMTALK.name())) {
            ImmutableMessageHubTemplate template = findMessageTemplate(request.get("code"));
            title = template.getTitle();
            message = template.getMessage();
        }

        setRecipients(messageType, title, message);
    }

    @Override
    protected MessageSpecification createMessageSpecification(String messageType, String title, String message, String mobile) throws ProcessException {
        return new MessageSpecification(request, messageType, title, message, mobile);
    }

    @Override
    protected void setDatabaseQueries() throws ProcessException {
        try {
            String sqlInsert = SQLXmlCache.instance().one(serial(SQLXML, "API-RESTFUL", "INSERT_MESSAGEHUB")).getStatement();
            String sqlCurrentSequence = SQLXmlCache.instance().one(serial(SQLXML, "API-RESTFUL", "SELECT_MESSAGEHUB_CURRENT_SEQUENCE")).getStatement();
            String sqlBatchSequence = SQLXmlCache.instance().one(serial(SQLXML, "API-RESTFUL", "SELECT_MESSAGEHUB_BATCH_SEQUENCE")).getStatement();
            databaseQueries = new DatabaseQueries(sqlInsert, sqlCurrentSequence, sqlBatchSequence);
        } catch (CacheableException e) {
            throw new ProcessException(e);
        }
    }

    @Override
    protected ImmutableMessageHubTemplate findMessageTemplate(String code) throws ProcessException {
        try {
            ImmutableMessageHubTemplate template = MessageHubTemplateCache.instance().one(serial(MESSAGEHUBTEMPLATE, code));

            if (template.getActivationOfUser() == DEACTIVATE) {
                throw new ProcessException("MessageHubCode[" + code + "] is deactivate.");
            }

            model.addResults(template.toString());
            return template;
        } catch (CacheableException e) {
            throw new ProcessException(e);
        }
    }

    @Override
    protected void setParameters(
            PreparedStatement pstmt
            , MessageSpecification messageSpecification
            , boolean isMulti
            , long sequence) throws ProcessException {
        try {
            //01 CLIENT_KEY / �����ĺ��� / [GETUMSKEY(P_SYSTEM_CODE NUMBER) �� 20�ڸ�] = YYYYMMDD(8�ڸ�) + �ý����ڵ�(2�ڸ�) + SEQUNECE
            //02 REQ_CH / �߼�ä�� / SMS, MMS, GDS(Global SMS), RCS, ALIMTALK, FRIENDTALK, PUSH, UMS, ARS(ALIMTALK/RCS/SMS or MMS)
            //03 TRAFFIC_TYPE / �߼۹�� / (normal:�Ϲ� (default), real:�ǽð�/�߿�, batch:������ �Ǵ� ����)
            //04 REQ_DATE / �߼ۿ�û�ð� / ����߼�(�̷��ð�)�� �ƴ� ��� ����ð�
            //05 CALLBACK_NUMBER / �߽Ź�ȣ
            //06 DEPT / �߽źμ�
            //07 PHONE / ������
            //08 MSG / ����
            //09 TITLE / ����
            //10 KAKAO_TEMPLATE_CODE / īī�����ø��ڵ�
            //11 ETC1 / �߼�ä���ڵ�
            //12 ETC3 / ��ġ�߼�SERIAL
            //13 ETC4 / ����ȣ
            //14 ETC5 / �����
            pstmt.setString(1, messageSpecification.senderChannel);
            pstmt.setString(2, messageSpecification.messageType);
            pstmt.setString(3, isMulti ? BATCH.name().toLowerCase() : REAL.name().toLowerCase());

            Timestamp reserveDate = null;

            if (messageSpecification.reserveDate != null) {
                reserveDate = new Timestamp(messageSpecification.reserveDate);
            }

            pstmt.setTimestamp(4, reserveDate);
            pstmt.setString(5, messageSpecification.sender);
            pstmt.setString(6, messageSpecification.br);
            pstmt.setString(7, messageSpecification.mobile);
            pstmt.setString(8, messageSpecification.message);
            pstmt.setString(9, messageSpecification.title);
            pstmt.setString(10, messageSpecification.fallbackChannel);
            pstmt.setString(11, messageSpecification.templateCode);
            pstmt.setString(12, messageSpecification.senderChannel);
            pstmt.setString(13, sequence == 0 ? null : String.valueOf(sequence));
            pstmt.setString(14, messageSpecification.custNumber);
            pstmt.setString(15, messageSpecification.user);
        } catch (SQLException e) {
            throw new ProcessException(e);
        }
    }

    @Override
    protected String getMobile(MessageSpecification messageSpecification) {
        return messageSpecification.mobile;
    }

    @Override
    protected String getMessage(MessageSpecification messageSpecification) {
        return messageSpecification.message;
    }

}
