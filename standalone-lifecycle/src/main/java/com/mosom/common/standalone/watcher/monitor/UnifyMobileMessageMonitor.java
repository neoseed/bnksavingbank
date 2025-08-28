/*
 * 프로그램명 : UnifyMobileMessageMonitor
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : UnifyMobileMessage Monitor
 */
package com.mosom.common.standalone.watcher.monitor;

import com.mosom.common.standalone.DataSourceProvider;
import com.mosom.common.standalone.ILifecycleOption;
import com.mosom.common.standalone.LifecycleException;
import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.Identifier;
import com.mosom.common.standalone.cache.document.SQLXmlCache;
import com.mosom.common.standalone.watcher.ScheduleWatcher;
import com.mosom.common.standalone.watcher.WatcherException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.mosom.common.standalone.LifecycleSituations.*;
import static com.mosom.common.standalone.cache.document.DocumentCacheTypeNames.SQLXML;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;
import static com.mosom.common.standalone.watcher.monitor.MonitorTypeNames.UMMMONITOR;

public class UnifyMobileMessageMonitor extends ScheduleWatcher<List<UnifyMobileMessageAgentStatus>> {

    private final DataSourceProvider dataSourceProvider;

    private final Date time = new Date();

    private UnifyMobileMessageAgentStatus activeAgent;
    private final UnifyMobileMessageAgentStatus ums;
    private final UnifyMobileMessageAgentStatus dkt;

    private static class UnifyMobileMessageMonitorHolder {

        private static final UnifyMobileMessageMonitor INSTANCE = new UnifyMobileMessageMonitor();

    }

    private UnifyMobileMessageMonitor() {
        dataSourceProvider = new DataSourceProvider();
        ums = dkt = new UnifyMobileMessageAgentStatus();
    }

    public static UnifyMobileMessageMonitor instance() {
        return UnifyMobileMessageMonitorHolder.INSTANCE;
    }

    @Override
    public void initialize(ILifecycleOption option) throws LifecycleException {
        if (isSituation(INITIALIZE, STARTING, RUNNING)) {
            log().info("{UnifyMobileMessageMonitor} is not shutdown situation.");
            return;
        }

        if (isSituation(SHUTDOWN)) {
            super.initialize(option);
            setRecipients();
            log().info("{UnifyMobileMessageMonitor} is initialize.");
        }
    }

    @Override
    public void start() {
        if (isSituation(STARTING, RUNNING)) {
            log().info("{UnifyMobileMessageMonitor} is already running situation.");
            return;
        }

        if (isSituation(SHUTDOWN)) {
            log().info("{UnifyMobileMessageMonitor} is not initialize.");
            return;
        }

        if (isSituation(INITIALIZE)) {
            time.setTime(System.currentTimeMillis());
            super.start();
            log().info("{UnifyMobileMessageMonitor} is running.");
        }
    }

    @Override
    public void shutdown() {
        if (isSituation(SHUTDOWN)) {
            log().info("{UnifyMobileMessageMonitor} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{UnifyMobileMessageMonitor} is already starting situation.");
            return;
        }

        if (isSituation(INITIALIZE, RUNNING)) {
            super.shutdown();
            log().info("{UnifyMobileMessageMonitor} is shutdown.");
        }
    }

    @Override
    public Date time() {
        return time;
    }

    @Override
    public MonitorTypeNames name() {
        return UMMMONITOR;
    }

    @Override
    public void clear() {
        dkt.clearRecipients();
        ums.clearRecipients();
    }

    @Override
    public void execute() throws WatcherException {
        try {
            executeMonitor();
            executeSendMessage();
        } finally {
            time.setTime(System.currentTimeMillis());
        }
    }

    @Override
    public List<UnifyMobileMessageAgentStatus> object() {
        return Arrays.asList(ums, dkt);
    }

    private void setRecipients() throws WatcherException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Identifier id = serial(SQLXML, "API-WATCHER", "UMMM_SELECT_RECIPIENT_MOBILE_NUMBERS");
            Connection con = dataSourceProvider.getConnection(true);
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                log().info("{UnifyMobileMessageMonitor} has no recipients.");
                return;
            }

            do {
                String recipient = rs.getString("RECIPIENT_MOBILE_NUMBER");
                ums.addRecipient(recipient);
                dkt.addRecipient(recipient);
            } while (rs.next());
        } catch (CacheableException e) {
            throw new WatcherException(e);
        } catch (SQLException e) {
            throw new WatcherException(e);
        } finally {
            dataSourceProvider.close(rs);
            dataSourceProvider.close(pstmt);
        }
    }

    private void executeMonitor() throws WatcherException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Identifier id = serial(SQLXML, "API-WATCHER", "UMMM_SELECT_CONDITION");
            Connection con = dataSourceProvider.getConnection(true);
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                setUnifyMobileMessageAgentStatus(ums, "UMS_", rs);
                setUnifyMobileMessageAgentStatus(dkt, "DKT_", rs);
            }

            activeAgent = ums.isTriggerSwitch() ? ums : dkt;
        } catch (CacheableException e) {
            throw new WatcherException(e);
        } catch (SQLException e) {
            throw new WatcherException(e);
        } finally {
            log().info("{UnifyMobileMessageMonitor} is operating properly.");
            dataSourceProvider.close(rs);
            dataSourceProvider.close(pstmt);
        }
    }

    private void executeSendMessage() throws WatcherException {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            if (activeAgent.isNotificationUse() && activeAgent.isNotification()) {
                Identifier id;

                //UMS 스위치 모드 일 때, UMS 장애 발생 시 DKT로 장애알림 발송
                //DKT 스위치 모드 일 때, DKT 장애 발생 시 UMS로 장애알림 발송
                if ("UMS".equals(activeAgent.getName())) {
                    id = serial(SQLXML, "API-WATCHER", "UMMM_DKT_INSERT_NOTIFICATION");
                } else {
                    id = serial(SQLXML, "API-WATCHER", "UMMM_UMS_INSERT_NOTIFICATION");
                }

                con = dataSourceProvider.getConnection(false);
                pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
                String statusMessage = activeAgent.getStatusMessage();

                for (String recipient : activeAgent.getRecipients()) {
                    pstmt.setString(1, recipient);
                    pstmt.setString(2, statusMessage);
                    pstmt.addBatch();
                    pstmt.clearParameters();
                }

                pstmt.executeBatch();
                log().info("{UnifyMobileMessageMonitor} send message.");
            }
        } catch (CacheableException e) {
            throw new WatcherException(e);
        } catch (SQLException e) {
            throw new WatcherException(e);
        } finally {
            dataSourceProvider.close(pstmt);
            dataSourceProvider.close(con);
        }
    }

    private void setUnifyMobileMessageAgentStatus(UnifyMobileMessageAgentStatus ummas, String prefix, ResultSet rs) throws SQLException {
        ummas.setNotificationUse(rs.getString("NOTIFICATION_USE").equals("ENABLED"));
        ummas.setAddreses(Arrays.asList(rs.getString("NOTIFICATION_SERVER_ADDRESS").split(";")));
        ummas.setServers(Arrays.asList(rs.getString("NOTIFICATION_SERVER_IP").split(";")));
        ummas.setPorts(Arrays.asList(rs.getString("NOTIFICATION_SERVER_PORT").split(";")));
        ummas.setName(rs.getString(prefix + "AGENT_NAME"));
        ummas.setTriggerSwitch(rs.getString(prefix + "STATUS").equals("ENABLED"));
        ummas.setTriggerWatcherSwitch(rs.getString(prefix + "WATCHER_STATUS").equals("ENABLED"));
        ummas.setOverallStatusCode(rs.getString(prefix + "OVERALL_STATUS"));
        ummas.setWaitingQueueCount(rs.getInt(prefix + "QUE_COUNT"));
        ummas.setAgentErrorLogCount(rs.getInt(prefix + "LOG_COUNT"));
    }

}
