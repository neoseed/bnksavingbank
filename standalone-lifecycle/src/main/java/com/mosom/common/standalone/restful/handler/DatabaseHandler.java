/*
 * 프로그램명 : DatabaseHandler
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 망내 시스템 REST API Service - 응답 제공자 Database
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.DataSourceProvider;
import com.mosom.common.standalone.DatabaseCommands;
import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.document.SQLXmlCache;
import com.mosom.common.standalone.restful.ProcessException;
import com.mosom.common.standalone.restful.ProcessingStatus;
import com.mosom.common.standalone.restful.RequestStructure;
import com.mosom.common.standalone.restful.ResponseModel;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mosom.common.standalone.cache.document.DocumentCacheTypeNames.SQLXML;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;

public class DatabaseHandler extends BaseProcessHandler {

    private enum ParameterTypes {

        STRING
        , BYTE, BYTES, SHORT, INT, LONG, FLOAT, DOUBLE, BIGDECIMAL
        , DATE, TIME, TIMESTAMP
        , BOOLEAN;

        static final String REGULAR_EXPRESSION;

        static {
            ParameterTypes[] types = values();

            StringBuilder builder = new StringBuilder();
            builder.append("(");

            for (int index = 0; index < types.length - 1; index++) {
                builder.append(types[index].name().toLowerCase());
                builder.append("|");
            }

            builder.append(types[types.length - 1].name().toLowerCase());
            builder.append(")-");

            REGULAR_EXPRESSION = builder.toString();
        }

    }

    /**
     * URI:/{0}/{1}/{2}/database[/{parameter(n)}/{value(n)}..*]
     * Example:/cbln/json/utf8/database
     *         /key/LOAN30.36_FindchkApplFmDate/command/select
     *         /bind1/long-76411111972/bind2/string-T1
     * 0:REQUESTER
     * 1:RESPONSE_TYPE
     * 2:RESPONSE_CHARSET
     * N:PARAMETER(n), VALUE(n)
     */
    @Override
    public ResponseModel execute() {
        try {
            executeQuery();
        } catch (ProcessException e) {
            e.printStackTrace();
            log().info("{DatabaseHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        }

        return model;
    }

    private void executeQuery() throws ProcessException {
        RequestStructure request = model.getRequestStructure();
        String key = request.get("key");

        if (key == null) {
            throw new ProcessException("['KEY'] parameter must be included.");
        }

        DataSourceProvider dataSourceProvider = new DataSourceProvider();
        Connection con = dataSourceProvider.getConnection(false);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            List<String> binds = request.getParameterContainsValues("bind");
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(serial(SQLXML, key)).getStatement());

            if (!binds.isEmpty()) {
                for (ListIterator<String> itr = binds.listIterator(); itr.hasNext();) {
                    setParameters(pstmt, itr.nextIndex() + 1, itr.next());
                }
            }

            switch (DatabaseCommands.valueOf(request.get("command").toUpperCase())) {
                case SELECT:
                    rs = pstmt.executeQuery();
                    inputResultSetOfColumnNameValuePair(rs, model);

                    if (model.isNotEmpty()) {
                        model.setProcessingStatus(ProcessingStatus.SUCCESS);
                    }

                    model.setProcessingStatusMessage("Result count:" + model.getResults().size());
                    break;
                case INSERT:
                case UPDATE:
                case DELETE:
                    model.setProcessingStatus(ProcessingStatus.FAILURE);
                    model.setProcessingStatusMessage("Not currently supported.");
                    break;
            }
        } catch (CacheableException e) {
            throw new ProcessException(e);
        } catch (SQLException e) {
            throw new ProcessException(e);
        } finally {
            dataSourceProvider.close(rs);
            dataSourceProvider.close(pstmt);
            dataSourceProvider.close(con);
        }
    }

    private void setParameters(PreparedStatement pstmt, int index, String valueSet) throws ProcessException {
        Pattern pattern = Pattern.compile(ParameterTypes.REGULAR_EXPRESSION);
        Matcher matcher = pattern.matcher(valueSet);
        String[] values = new String[2];

        if (matcher.find()) {
            values[0] = valueSet.substring(0, matcher.group().length() - 1);
            values[1] = valueSet.substring(matcher.group().length());
        } else {
            throw new ProcessException(valueSet + " pattern not found.");
        }

        try {
            ParameterTypes type = ParameterTypes.valueOf(values[0].toUpperCase());
            String value = values[1];

            switch (type) {
                case STRING:
                    pstmt.setString(index, value);
                    break;
                case BYTE:
                    pstmt.setByte(index, Byte.parseByte(value));
                    break;
                case BYTES:
                    pstmt.setBytes(index, value.getBytes());
                    break;
                case SHORT:
                    pstmt.setShort(index, Short.parseShort(value));
                    break;
                case INT:
                    pstmt.setInt(index, Integer.parseInt(value));
                    break;
                case LONG:
                    pstmt.setLong(index, Long.parseLong(value));
                    break;
                case FLOAT:
                    pstmt.setFloat(index, Float.parseFloat(value));
                    break;
                case DOUBLE:
                    pstmt.setDouble(index, Double.parseDouble(value));
                    break;
                case BIGDECIMAL:
                    pstmt.setBigDecimal(index, new BigDecimal(value));
                    break;
                case DATE:
                    pstmt.setDate(index, convertDate(value));
                    break;
                case TIME:
                    pstmt.setTime(index, convertTime(value));
                    break;
                case TIMESTAMP:
                    pstmt.setTimestamp(index, convertTimestamp(value));
                    break;
                case BOOLEAN:
                    pstmt.setBoolean(index, Boolean.parseBoolean(value));
                    break;
            }
        } catch (SQLException e) {
            throw new ProcessException(e);
        }
    }

    public static void inputResultSetOfColumnNameValuePair(ResultSet rs, ResponseModel model) throws SQLException {
        inputResultSetOfColumnNameValuePair(rs, model, true);
    }

    public static void inputResultSetOfColumnNameValuePair(ResultSet rs, ResponseModel model, boolean isColumnOrder) throws SQLException {
        inputResultSetOfColumnNameValuePair(rs, model, isColumnOrder, null);
    }

    public static void inputResultSetOfColumnNameValuePair(ResultSet rs, ResponseModel model, boolean isColumnOrder, String resultKey) throws SQLException {
        if (rs.next()) {
            Map<String, String> camelNames = new LinkedHashMap<String, String>();
            int columns = rs.getMetaData().getColumnCount();

            for (int index = 1; index <= columns; index++) {
                String name = rs.getMetaData().getColumnName(index);
                camelNames.put("columnOrder" + index, convertToCamelCase(name));
            }

            if (isColumnOrder) {
                model.addResults(camelNames);
            }

            do {
                Iterator<String> itr = camelNames.keySet().iterator();
                Map<String, String> values = new LinkedHashMap<String, String>();

                for (int index = 1; index <= columns; index++) {
                    String value = rs.getString(index);

                    if (value == null) {
                        value = "";
                    }

                    values.put(camelNames.get(itr.next()), value);
                }

                if (resultKey != null) {
                    model.addResults(resultKey, values);
                } else {
                    model.addResults(values);
                }
            } while (rs.next());
        }
    }

    public static String convertToCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        String[] words = input.toLowerCase().split("[ _]");

        for (int index = 0; index < words.length; index++) {
            String word = words[index];

            if (index == 0) {
                result.append(word);
            } else {
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
        }

        return result.toString();
    }

    private Date convertDate(String parameter) throws ProcessException {
        if (isMatchNumber(parameter)) {
            return new Date(Long.parseLong(parameter));
        }

        if (parameter.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return Date.valueOf(parameter);
        }

        throw new ProcessException(parameter + " pattern not format.");
    }

    private Time convertTime(String parameter) throws ProcessException {
        if (isMatchNumber(parameter)) {
            return new Time(Long.parseLong(parameter));
        }

        if (parameter.matches("\\d{2}:\\d{2}:\\d{2}")) {
            return Time.valueOf(parameter);
        }

        throw new ProcessException(parameter + " pattern not format.");
    }

    private Timestamp convertTimestamp(String parameter) throws ProcessException {
        if (isMatchNumber(parameter)) {
            return new Timestamp(Long.parseLong(parameter));
        }

        if (parameter.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            return Timestamp.valueOf(parameter);
        }

        throw new ProcessException(parameter + " pattern not format.");
    }

    private boolean isMatchNumber(String parameter) {
        return parameter.matches("\\d+");
    }

}
