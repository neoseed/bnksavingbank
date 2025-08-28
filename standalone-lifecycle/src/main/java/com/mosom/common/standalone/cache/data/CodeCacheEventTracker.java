/*
 * 프로그램명 : CodeCacheEventTracker
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.13)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.13)
 * 적　　　요 : Database(Table Trigger) Event Tracker
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.DataSourceProvider;
import com.mosom.common.standalone.DatabaseCommands;
import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.Identifier;
import com.mosom.common.standalone.cache.ScheduleEventTracker;
import com.mosom.common.standalone.cache.document.SQLXmlCache;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mosom.common.standalone.LifecycleSituations.RUNNING;
import static com.mosom.common.standalone.cache.data.DataCacheTypeNames.CODE;
import static com.mosom.common.standalone.cache.document.DocumentCacheTypeNames.SQLXML;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;

public class CodeCacheEventTracker extends ScheduleEventTracker {

    private final DataSourceProvider dataSourceProvider;

    private final Date time = new Date();

    private static class CodeCacheDataEventTrackerHolder {

        private static final CodeCacheEventTracker INSTANCE = new CodeCacheEventTracker();

    }

    public static CodeCacheEventTracker instance() {
        return CodeCacheDataEventTrackerHolder.INSTANCE;
    }

    private CodeCacheEventTracker() {
        dataSourceProvider = new DataSourceProvider();
    }

    @Override
    public DataCacheTypeNames name() {
        return CODE;
    }

    @Override
    protected void clear() {
        Connection con = dataSourceProvider.getConnection(false);
        PreparedStatement pstmt = null;

        try {
            //TODO:THOMAS-PK:2023-08-09:API-CACHE.DELETE_CODE_HISTORY SQL 정의되어 있지 않음
            //3일 전 시점부터 과거의 데이터를 삭제
            Identifier id = serial(SQLXML, "API-CACHE", "DELETE_CODE_HISTORY");
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
            pstmt.executeUpdate();
        } catch (CacheableException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSourceProvider.close(pstmt);
            dataSourceProvider.close(con);
        }
    }

    @Override
    protected void load() {
        if (CodeCache.instance().situation() != RUNNING) {
            return;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            //TODO:THOMAS-PK:2023-08-09:API-CACHE.SELECT_CODE_HISTORY SQL 정의되어 있지 않음
            Identifier id = serial(SQLXML, "API-CACHE", "SELECT_CODE_HISTORY");
            Connection con = dataSourceProvider.getConnection(true);
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
            pstmt.setBigDecimal(1, new BigDecimal(maxTimestamp));
            rs = pstmt.executeQuery();

            if (rs.next()) {
                List<CodeHistory> models = new ArrayList<CodeHistory>();
                BigInteger lastTimestamp;

                do {
                    CodeHistory history = build(rs);
                    ImmutableCode code = history.getModel();
                    lastTimestamp = history.getTimestamp();

                    if (CodeCache.instance().isStorage(code)) {
                        CodeCache.instance().unstorage(code);
                        continue;
                    }

                    models.add(history);
                } while (rs.next());

                maxTimestamp = lastTimestamp;
                renewal(models);
            }
        } catch (CacheableException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            time.setTime(System.currentTimeMillis());
            dataSourceProvider.close(rs);
            dataSourceProvider.close(pstmt);
        }
    }

    private CodeHistory build(ResultSet rs) throws CacheableException {
        try {
            BigInteger timestamp = rs.getBigDecimal("HISTORY_TIMESTAMP").toBigInteger();
            DatabaseCommands command = DatabaseCommands.valueOf(rs.getString("HISTORY_COMMAND"));

            Code model = new Code();
            model.setCategory(ImmutableCode.Category.find(rs.getString("ENTITY")));
            model.setKey1(rs.getString("KEY1"));
            model.setKey2(rs.getString("KEY2"));
            model.setOrderSequence(rs.getInt("ORDER_SEQUENCE"));
            model.setComment1(rs.getString("COMMENT1"));
            model.setComment1(rs.getString("COMMENT2"));
            model.setComment1(rs.getString("COMMENT3"));
            model.setOption1(rs.getString("ID1"));
            model.setOption2(rs.getString("ID2"));
            model.setOption3(rs.getString("ID3"));
            model.setOption4(rs.getString("ID4"));
            model.setOption5(rs.getString("ID5"));
            model.setOption6(rs.getString("ID6"));
            model.setIdentifier(serial(name(), model.getKey1(), model.getKey2()));

            return new CodeHistory(timestamp, command, model);
        } catch (SQLException e) {
            throw new CacheableException(e);
        }
    }

    private void renewal(List<CodeHistory> models) {
        for (CodeHistory model : models) {
            switch (model.getCommand()) {
                case DELETE:
                    CodeCache.instance().remove(model.getModel());
                    break;
                case INSERT:
                    CodeCache.instance().set(model.getModel(), true);
                    break;
                case UPDATE:
                    CodeCache.instance().set(model.getModel(), false);
                    break;
            }
        }
    }

}
