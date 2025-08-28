/*
 * 프로그램명 : MobileMessageHandler
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 제공자 MobileMessage(발송 모듈 공통)
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.DataSourceProvider;
import com.mosom.common.standalone.cache.ImmutableModel;
import com.mosom.common.standalone.restful.ProcessException;
import com.mosom.common.standalone.restful.ProcessingStatus;
import com.mosom.common.standalone.restful.RequestStructure;
import com.mosom.common.standalone.restful.ResponseModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cv.jff.common.util.Converter.formatNumber;
import static java.lang.Thread.currentThread;

abstract public class MobileMessageHandler<T> extends BaseProcessHandler {

    enum SendRanges {

        /**
         * RANGE_SINGLE      : 100개 이하
         * RANGE_MULTI       : 5000개 이하
         * RANGE_MULTI_ASYNC : 5000개 이상 비동기 대량발송
         */
        RANGE_SINGLE(100)
        , RANGE_MULTI(5000)
        , RANGE_MULTI_ASYNC(Integer.MAX_VALUE);

        final int size;

        SendRanges(int size) {
            this.size = size;
        }

        static SendRanges range(int size) {
            for (SendRanges range : SendRanges.values()) {
                if (size <= range.size) {
                    return range;
                }
            }

            return RANGE_MULTI_ASYNC;
        }
    }

    static class DatabaseQueries {

        final String sqlInsert;

        final String sqlCurrentSequence;

        final String sqlBatchSequence;

        DatabaseQueries(String sqlInsert, String sqlCurrentSequence, String sqlBatchSequence) {
            this.sqlInsert = sqlInsert;
            this.sqlCurrentSequence = sqlCurrentSequence;
            this.sqlBatchSequence = sqlBatchSequence;
        }

    }

    class MessageSpecificationBatch {

        final long batchSequence;

        final List<List<T>> units;

        private MessageSpecificationBatch() throws ProcessException {
            batchSequence = getBatchSequence();
            units = units();
        }

        private List<List<T>> units() {
            List<List<T>> units = new ArrayList<List<T>>();
            int total = messageSpecifications.size();
            int unit = SendRanges.RANGE_MULTI.size;

            for (int start = 0; start < total; start += unit) {
                int end = Math.min(start + unit, total);
                units.add(new ArrayList<T>(messageSpecifications.subList(start, end)));
            }

            return units;
        }

        private Map<ExecutorService, Runnable> tasks() {
            Map<ExecutorService, Runnable> tasks = new HashMap<ExecutorService, Runnable>();
            AtomicInteger waitingTime = new AtomicInteger(0);

            for (final List<T> unit : units) {
                final int currentUnitWaitingTime = waitingTime.getAndAdd(10);
                final ExecutorService executor = Executors.newSingleThreadExecutor();
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.SECONDS.sleep(currentUnitWaitingTime);
                        } catch (InterruptedException e) {
                            log().info("{" + provider() + "} " + e.getMessage());
                        }

                        try {
                            sendToMulti(unit, batchSequence);
                        } catch (ProcessException e) {
                            e.printStackTrace();
                            log().info("{" + provider() + "} " + e.getMessage());
                        } finally {
                            executor.shutdown();
                            log().info("{" + provider() + "} Send message to batch(sequence:" + batchSequence + ") process(ID:" + currentThread().getId() + ", Job-Count:" + formatNumber(unit.size(), "#,###") + ") completed.");
                            log().info("{" + provider() + "} Send message to batch(sequence:" + batchSequence + ") process(ID:" + currentThread().getId() + ") shutdown(" + executor.isShutdown() + ").");
                        }
                    }
                };
                tasks.put(executor, task);
            }

            return tasks;
        }

    }

    protected RequestStructure request;

    protected final DataSourceProvider dataSourceProvider;

    protected DatabaseQueries databaseQueries;

    protected List<T> messageSpecifications;

    private static final ThreadLocal<SimpleDateFormat> RESERVE_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };


    public MobileMessageHandler() {
        dataSourceProvider = new DataSourceProvider();
        messageSpecifications = new ArrayList<T>();
    }

    @Override
    public ResponseModel execute() {
        try {
            send();

            if (model.isNotEmpty()) {
                model.setProcessingStatus(ProcessingStatus.SUCCESS);
                model.setProcessingStatusMessage("OK");
            }
        } catch (ProcessException e) {
            e.printStackTrace();
            log().info("{" + provider() + "} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        }

        return model;
    }

    private void send() throws ProcessException {
        request = model.getRequestStructure();
        validation();
        setMessageContents();
        setDatabaseQueries();

        switch (SendRanges.range(messageSpecifications.size())) {
            //100건 이하 발송의 경우, 개별 단건발송
            case RANGE_SINGLE:
                sendToSingle();
                break;
            //5000건 이하 대량발송의 경우, 작업 효율을 위해 배치동작 수행
            //배치동작은 증가된 SERIAL_SEQUENCE 값을 알 수 없으므로 Mobile로 대체
            case RANGE_MULTI:
                sendToMulti();
                break;
            //5000건 이상 대량발송의 경우, 작업 효율을 위해 비동기 배치동작 수행
            //비동기 배치동작은 증가된 SERIAL_SEQUENCE 값을 알 수 없으므로 BATCH_SEQUENCE로 대체
            case RANGE_MULTI_ASYNC:
                sendToMultiAsync();
                break;
        }
    }

    protected void setRecipients(String messageType, String title, String message) throws ProcessException {
        //수신자 Collection 경우, 개별 순번을 부여하여 Parameter 설정 후 Collection 삭제
        if (request.isParameterContainsKey("recipients")) {
            AtomicInteger order = new AtomicInteger(1);

            for (String mobile : request.get("recipients").split(";")) {
                request.set("recipient" + order.getAndAdd(1), mobile.replaceAll("\\D", ""));
            }

            request.remove("recipients");
        }

        //변수-수신자 Collection 형태의 경우(Template에 변수와 수신자가 다른 조합), 개별 조립
        //수신자 Collection 경우(Template에 수신자만 다른 경우), 수신자 기준으로 조립
        if (request.isParameterContainsKey("bind-recipients")) {
            for (String bindRecipients : request.get("bind-recipients").split("\\|")) {
                String[] parameters = bindRecipients.split(";");
                String mobile = parameters[parameters.length - 1].replaceAll("\\D", "");
                List<String> binds = Arrays.asList(Arrays.copyOf(parameters, parameters.length - 1));
                messageSpecifications.add(createMessageSpecification(messageType, title, replaceParameter(message, binds), mobile));
            }
        } else {
            List<String> mobiles = request.getParameterContainsValues("recipient");
            List<String> binds = request.getParameterContainsValues("bind");
            message = replaceParameter(message, binds);

            for (String mobile : mobiles) {
                messageSpecifications.add(createMessageSpecification(messageType, title, message, mobile.replaceAll("\\D", "")));
            }
        }

        if (messageSpecifications.isEmpty()) {
            throw new ProcessException("[MessageSpecification] not specified.");
        }
    }

    private static String replaceParameter(String message, List<String> binds) throws ProcessException {
        Matcher matcher = Pattern.compile("#\\{([^}]*)}").matcher(message);
        int matches = 0;

        while (matcher.find()) {
            matches++;
        }

        if (matches == 0 && binds.isEmpty()) {
            return message;
        }

        if (matches != binds.size()) {
            throw new ProcessException("Parameter of replacements does not match.");
        }

        matcher.reset();
        String replace = message;
        Iterator<String> itr = binds.iterator();

        try {
            while (matcher.find()) {
                replace = replace.replace(matcher.group(), itr.next());
            }
        } catch (NoSuchElementException e) {
            throw new ProcessException("Parameter of replacements does not match.", e);
        }

        return replace;
    }

    private void sendToSingle() throws ProcessException {
        Map<String, String> results = new LinkedHashMap<String, String>();
        Connection con = dataSourceProvider.getConnection(false);
        PreparedStatement pstmt = null;

        try {
            for (T messageSpecification : messageSpecifications) {
                pstmt = con.prepareStatement(databaseQueries.sqlInsert);
                setParameters(pstmt, messageSpecification, false, 0);

                if (pstmt.executeUpdate() != 0) {
                    dataSourceProvider.close(pstmt);
                    pstmt = con.prepareStatement(databaseQueries.sqlCurrentSequence);
                    ResultSet rs = pstmt.executeQuery();
                    long serialSequence = 0;

                    if (rs.next()) {
                        serialSequence = rs.getLong("SERIAL_SEQUENCE");
                        results.put("serialSequence" + serialSequence, getMessage(messageSpecification));
                        dataSourceProvider.close(rs);
                        dataSourceProvider.close(pstmt);
                    }

                    log().info("{" + provider() + "} Send message to single(" + serialSequence + ") process completed.");
                }
            }
        } catch (SQLException e) {
            throw new ProcessException(e);
        } finally {
            dataSourceProvider.close(pstmt);
            dataSourceProvider.close(con);
        }

        model.addResults(results);
    }

    private void sendToMulti() throws ProcessException {
        sendToMulti(Collections.<T>emptyList(), 0);
    }

    private void sendToMulti(List<T> asyncBatchUnit, long batchSequence) throws ProcessException {
        boolean isManualBatchSequence = batchSequence == 0;
        long manualBatchSequence = 0;

        Map<String, String> results = new LinkedHashMap<String, String>();
        Connection con = dataSourceProvider.getConnection(false);
        PreparedStatement pstmt = null;

        if (isManualBatchSequence) {
            manualBatchSequence = getBatchSequence(con);
        }

        try {
            pstmt = con.prepareStatement(databaseQueries.sqlInsert);

            if (asyncBatchUnit.isEmpty()) {
                for (T messageSpecification : messageSpecifications) {
                    setParameters(pstmt, messageSpecification, true, isManualBatchSequence ? manualBatchSequence : batchSequence);
                    results.put(getMobile(messageSpecification), getMessage(messageSpecification));
                    pstmt.addBatch();
                    pstmt.clearParameters();
                }
            } else {
                for (T messageSpecification : asyncBatchUnit) {
                    setParameters(pstmt, messageSpecification, true, isManualBatchSequence ? manualBatchSequence : batchSequence);
                    results.put(getMobile(messageSpecification), getMessage(messageSpecification));
                    pstmt.addBatch();
                    pstmt.clearParameters();
                }
            }

            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new ProcessException(e);
        } finally {
            dataSourceProvider.close(pstmt);
            dataSourceProvider.close(con);
        }

        model.addResults(results);

        if (isManualBatchSequence) {
            model.addResults("serialBatchSequence" + manualBatchSequence);
            log().info("{" + provider() + "} Send message to batch process(Job-Count:" + formatNumber(results.size(), "#,###") + ") completed.");
        }
    }

    private void sendToMultiAsync() throws ProcessException {
        MessageSpecificationBatch input = new MessageSpecificationBatch();
        Map<ExecutorService, Runnable> tasks = input.tasks();

        for (ExecutorService executor : tasks.keySet()) {
            executor.execute(tasks.get(executor));
        }

        model.addResults("serialBatchSequence" + input.batchSequence);
    }

    private long getBatchSequence() throws ProcessException {
        return getBatchSequence(null);
    }

    private long getBatchSequence(Connection link) throws ProcessException {
        boolean isNotLink = link == null;
        Connection con = isNotLink ? dataSourceProvider.getConnection(false) : link;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement(databaseQueries.sqlBatchSequence);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("SERIAL_SEQUENCE");
            }
        } catch (SQLException e) {
            throw new ProcessException(e);
        } finally {
            dataSourceProvider.close(rs);
            dataSourceProvider.close(pstmt);

            if (isNotLink) {
                dataSourceProvider.close(con);
            }
        }

        throw new ProcessException("Failed to issue batch-sequence.");
    }

    protected static Long parseReserveDate(String date) throws ProcessException {
        if (date == null) {
            return null;
        }

        try {
            return RESERVE_DATE_FORMAT.get().parse(date).getTime();
        } catch (ParseException e) {
            throw new ProcessException(e);
        }
    }


    abstract protected String provider();

    abstract protected void validation() throws ProcessException;

    abstract protected void setMessageContents() throws ProcessException;

    abstract protected void setDatabaseQueries() throws ProcessException;

    abstract protected T createMessageSpecification(String messageType, String title, String message, String mobile) throws ProcessException;

    abstract protected ImmutableModel findMessageTemplate(String code) throws ProcessException;

    abstract protected void setParameters(
            PreparedStatement pstmt
            , T messageSpecification
            , boolean isMulti
            , long sequence) throws ProcessException;

    abstract protected String getMobile(T messageSpecification);

    abstract protected String getMessage(T messageSpecification);

}
