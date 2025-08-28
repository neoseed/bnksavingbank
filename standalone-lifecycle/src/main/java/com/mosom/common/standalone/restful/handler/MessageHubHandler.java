/*
 * 프로그램명 : MessageHubHandler
 * 설　계　자 : Thomas Parker(임예준) - (2025.07.09)
 * 작　성　자 : Thomas Parker(임예준) - (2025.07.09)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 제공자 MessageHub(LGU+)
 * Kakao, RCS, SMS/MMS 통합 발송 및 ARS(ALIMTALK->RCS->SMS/MMS) Fallback
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
 *         /bind1/임예준/bind2/130-00-00-0000000/bind3/0000-00-00
 *         /recipient1/01000000001/recipient2/01000000002
 * Example Message code:202202210052(적금이연만기안내)
 * [BNK저축은행] #{고객명}님의 #{계좌번호} #{일자} 만기이연 (가족대리 해지시 위임서류필요)
 * 0:Requester
 * 1:Response Type
 * 2:Response Charset
 * messagehub:Response Provider
 * N:PARAMETER(n), VALUE(n)
 * Requester, Response Type, Response Charset, Response Provider를 제외한 모든 Parameter는 POST 방식 사용 가능
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
         * ALIMTALK   : Kakao 알림톡
         * FRIENDTALK : Kakao 친구톡
         * PUSH       : 앱푸시
         * UMS        : 통합 템플릿
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

        //다른 Service Domain에서 호출할 경우 Requesters.{NAME}.channelCode() 값을 할당
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
        //Message Type 확인
        MessageTypes messageType = validateAndNormalizeMessageType();
        //Template Code, Title, Message 확인
        validateMessageContents(messageType);
        //수신자 확인
        validateRecipients();
        //Fallback 확인
        validateAndSetFallback(messageType);
    }

    private MessageTypes validateAndNormalizeMessageType() throws ProcessException {
        String messageType = request.get(Preconditions.type.name());

        if (messageType == null) {
            throw new ProcessException("[MessageType] not specified.");
        }

        try {
            messageType = messageType.toUpperCase();
            MessageTypes result = MessageTypes.valueOf(messageType); // 유효성 검사
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
            //fallback 미지정 시 기본값 설정
            setDefaultFallback(messageType);
            return;
        }

        //사용자가 지정한 fallback 유효성 검사
        FallbackTypes specifiedFallbackType;

        try {
            specifiedFallbackType = FallbackTypes.valueOf(fallbackType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ProcessException("[FallbackType] not specified.(" + e.getMessage() + ")", e);
        }

        // NONE은 항상 허용
        if (FallbackTypes.NONE == specifiedFallbackType) {
            request.set("fallback", specifiedFallbackType.name());
            return;
        }

        // 메시지 타입과 호환성 검사
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
                //나머지 유형은 fallback 의미가 없으므로 NONE으로 강제 설정
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

        //ALIMTALK의 경우 Title, 바인딩 Message 할당
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
            //01 CLIENT_KEY / 고유식별자 / [GETUMSKEY(P_SYSTEM_CODE NUMBER) 총 20자리] = YYYYMMDD(8자리) + 시스템코드(2자리) + SEQUNECE
            //02 REQ_CH / 발송채널 / SMS, MMS, GDS(Global SMS), RCS, ALIMTALK, FRIENDTALK, PUSH, UMS, ARS(ALIMTALK/RCS/SMS or MMS)
            //03 TRAFFIC_TYPE / 발송방식 / (normal:일반 (default), real:실시간/중요, batch:마케팅 또는 광고)
            //04 REQ_DATE / 발송요청시간 / 예약발송(미래시간)이 아닌 경우 현재시간
            //05 CALLBACK_NUMBER / 발신번호
            //06 DEPT / 발신부서
            //07 PHONE / 수신자
            //08 MSG / 본문
            //09 TITLE / 제목
            //10 KAKAO_TEMPLATE_CODE / 카카오템플릿코드
            //11 ETC1 / 발송채널코드
            //12 ETC3 / 배치발송SERIAL
            //13 ETC4 / 고객번호
            //14 ETC5 / 담당자
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
