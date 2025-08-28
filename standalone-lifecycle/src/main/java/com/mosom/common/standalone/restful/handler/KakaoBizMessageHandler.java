/*
 * ���α׷��� : KakaoBizMessageHandler
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.03.08)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.03.08)
 * ���������� : �������� �ý��� REST API Service - ���� ������ KakaoBizMessage
 * Kakao ���� �߼� �� SMS/MMS Fallback
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.data.ImmutableKakaoBizMessageTemplate;
import com.mosom.common.standalone.cache.data.KakaoBizMessageTemplateCache;
import com.mosom.common.standalone.cache.document.SQLXmlCache;
import com.mosom.common.standalone.restful.ProcessException;
import com.mosom.common.standalone.restful.RequestStructure;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.mosom.common.standalone.cache.data.DataCacheTypeNames.KKOBIZMESSAGETEMPLATE;
import static com.mosom.common.standalone.cache.data.ImmutableKakaoBizMessageTemplate.Activations.DEACTIVATE;
import static com.mosom.common.standalone.cache.data.ImmutableKakaoBizMessageTemplate.SendTypes.MULTI;
import static com.mosom.common.standalone.cache.data.ImmutableKakaoBizMessageTemplate.SendTypes.SINGLE;
import static com.mosom.common.standalone.cache.document.DocumentCacheTypeNames.SQLXML;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;

/**
 * URI:/{0}/{1}/{2}/kkobizmessage[/{parameter(n)}/{value(n)}..*]
 * Example:/rpa/json/utf8/kkomessage
 *         /type/kx/code/202202210052
 *         /bind1/�ӿ���/bind2/130-00-00-0000000/bind3/0000-00-00
 *         /recipient1/01000000001/recipient2/01000000002
 * Example Message code:202202210052(�����̿�����ȳ�)
 * [BNK��������] #{����}���� #{���¹�ȣ} #{����} �����̿� (�����븮 ������ ���Ӽ����ʿ�)
 * 0:Requester
 * 1:Response Type
 * 2:Response Charset
 * kkobizmessage:Response Provider
 * N:PARAMETER(n), VALUE(n)
 * Requester, Response Type, Response Charset, Response Provider�� ������ ��� Parameter�� POST ��� ��� ����
 */
public class KakaoBizMessageHandler extends MobileMessageHandler<KakaoBizMessageHandler.MessageSpecification> {

    public enum Preconditions {

        /**
         * type : Message Type
         * code : Template Code
         */
        type, code

    }

    public enum MessageTypes {

        /**
         * KX : Push Talk ����
         * KS : Push Talk fallback �δ� SMS(�ܹ�)
         * KM : Push Talk fallback �δ� LMS(�幮)
         * XS : SMS(�ܹ�) ����
         * XM : LMS(�幮) ����
         */
        KX, KS, KM, XS, XM

    }


    static class MessageSpecification {

        final String messageType;

        final String templateCode;

        final String title;

        final String message;

        final String mobile;


        //�ٸ� Service Domain���� ȣ���� ��� Requesters.{NAME}.channelCode() ���� �Ҵ�
        final String senderChannel;

        String sender;

        Long reserveDate;

        String br;

        String user;

        String custNumber;

        String optionalButtonLink1;

        String optionalButtonLink2;

        String optionalButtonLink3;

        String optionalButtonLink4;

        String optionalButtonLink5;

        MessageSpecification(RequestStructure request, String messageType, String title, String message, String mobile) throws ProcessException {
            this.messageType = messageType;
            this.message = message;
            this.title = title;
            this.mobile = mobile;
            this.senderChannel = request.getRequester().channelCode();
            templateCode = request.get("code");
            sender = request.get("sender");
            br = request.get("br");
            user = request.get("user");
            custNumber = request.get("custNumber");
            optionalButtonLink1 = request.get("optionalButtonLink1");
            optionalButtonLink2 = request.get("optionalButtonLink2");
            optionalButtonLink3 = request.get("optionalButtonLink3");
            optionalButtonLink4 = request.get("optionalButtonLink4");
            optionalButtonLink5 = request.get("optionalButtonLink5");

            if (request.get("reserveDate") != null) {
                reserveDate = parseReserveDate(request.get("reserveDate"));
            }
        }

    }

    public KakaoBizMessageHandler() {
        super();
    }

    @Override
    protected String provider() {
        return getClass().getSimpleName();
    }

    @Override
    protected void validation() throws ProcessException {
        //Message Type Ȯ��
        validateAndNormalizeMessageType();
        //Template Code Ȯ��
        validateMessageContents();
        //������ Ȯ��
        validateRecipients();
    }

    private void validateAndNormalizeMessageType() throws ProcessException {
        String messageType = request.get(Preconditions.type.name());

        if (messageType == null) {
            throw new ProcessException("[MessageType] not specified.");
        }

        try {
            messageType = messageType.toUpperCase();
            MessageTypes.valueOf(messageType); // ��ȿ�� �˻�
            request.set(MessageHubHandler.Preconditions.type.name(), messageType);
        } catch (IllegalArgumentException e) {
            throw new ProcessException("[MessageType] not specified.(" + e.getMessage() + ")", e);
        }
    }

    private void validateMessageContents() throws ProcessException {
        if (request.get(Preconditions.code.name()) == null) {
            throw new ProcessException("[TemplateCode] not specified.");
        }
    }

    private void validateRecipients() throws ProcessException {
        if (!request.isParameterContainsCharacter("recipient")) {
            throw new ProcessException("[RECIPIENT] parameter not specified.");
        }
    }

    @Override
    protected void setMessageContents() throws ProcessException {
        String messageType = request.get(Preconditions.type.name());

        ImmutableKakaoBizMessageTemplate template = findMessageTemplate(request.get("code"));
        String title = template.getTitle();
        String message = template.getMessage();

        setRecipients(messageType, title, message);
    }

    @Override
    protected MessageSpecification createMessageSpecification(String messageType, String title, String message, String mobile) throws ProcessException {
        return new MessageSpecification(request, messageType, title, message, mobile);
    }

    @Override
    protected void setDatabaseQueries() throws ProcessException {
        try {
            String sqlInsert = SQLXmlCache.instance().one(serial(SQLXML, "API-RESTFUL", "INSERT_KKO_MESSAGE")).getStatement();
            String sqlCurrentSequence = SQLXmlCache.instance().one(serial(SQLXML, "API-RESTFUL", "SELECT_KKO_CURRENT_SEQUENCE")).getStatement();
            String sqlBatchSequence = SQLXmlCache.instance().one(serial(SQLXML, "API-RESTFUL", "SELECT_KKO_BATCH_SEQUENCE")).getStatement();
            databaseQueries = new DatabaseQueries(sqlInsert, sqlCurrentSequence, sqlBatchSequence);
        } catch (CacheableException e) {
            throw new ProcessException(e);
        }
    }

    @Override
    protected ImmutableKakaoBizMessageTemplate findMessageTemplate(String code) throws ProcessException {
        try {
            ImmutableKakaoBizMessageTemplate template = KakaoBizMessageTemplateCache.instance().one(serial(KKOBIZMESSAGETEMPLATE, code));

            if (template.getActivationOfUser() == DEACTIVATE) {
                throw new ProcessException("KakaoBizMessageCode[" + code + "] is deactivate.");
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
            pstmt.setString(1, messageSpecification.message);
            pstmt.setString(2, messageSpecification.title);
            pstmt.setString(3, messageSpecification.messageType);
            pstmt.setString(4, messageSpecification.mobile);
            pstmt.setString(5, messageSpecification.sender);
            pstmt.setString(6, isMulti ? MULTI.code() : SINGLE.code());
            pstmt.setString(7, messageSpecification.templateCode);

            Timestamp reserveDate = null;

            if (messageSpecification.reserveDate != null) {
                reserveDate = new Timestamp(messageSpecification.reserveDate);
            }

            pstmt.setTimestamp(8, reserveDate);
            pstmt.setString(9, messageSpecification.senderChannel);
            pstmt.setString(10, messageSpecification.br);
            pstmt.setString(11, messageSpecification.user);
            pstmt.setString(12, messageSpecification.custNumber);
            pstmt.setString(13, sequence == 0 ? null : String.valueOf(sequence));
            pstmt.setString(14, messageSpecification.optionalButtonLink1);
            pstmt.setString(15, messageSpecification.optionalButtonLink2);
            pstmt.setString(16, messageSpecification.optionalButtonLink3);
            pstmt.setString(17, messageSpecification.optionalButtonLink4);
            pstmt.setString(18, messageSpecification.optionalButtonLink5);
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
