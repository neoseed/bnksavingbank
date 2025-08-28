/*
 * 프로그램명 : WatcherStarter
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 독립 Watcher 서비스의 실행
 */
package com.mosom.common.standalone.watcher.helper;

import com.mosom.common.standalone.*;
import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.Identifier;
import com.mosom.common.standalone.cache.document.SQLXmlCache;
import com.mosom.common.standalone.watcher.WatcherProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mosom.common.standalone.OptionGenerator.schedule;
import static com.mosom.common.standalone.cache.document.DocumentCacheTypeNames.SQLXML;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;

public class WatcherStarter implements ILifecycleStarter {

    //MOSOMIII.V_CODEMS(E_SYSCOD)
    //ID_LIFECYCLE_WATCHER:UnifyMobileMessageMonitor:110
    //ID_LIFECYCLE_WATCHER:...                      :...
    @Override
    public void run(String group) {
        for (LifecycleProfile profile : lifecycleProfiles(group)) {
            if (isRunningTarget((WatcherProfile) profile)) {
                try {
                    profile.lifecycle.initialize(profile.option);
                    profile.lifecycle.start();
                } catch (LifecycleException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public List<LifecycleProfile> lifecycleProfiles(String group) {
        List<LifecycleProfile> profiles = new ArrayList<LifecycleProfile>();
        DataSourceProvider dataSourceProvider = new DataSourceProvider();
        Connection con = dataSourceProvider.getConnection(false);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Identifier id = serial(SQLXML, "API-LIFECYCLE", "SELECT_ALL_LIFECYCLE_CONTROL");
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
            pstmt.setString(1, group);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                boolean enable = Boolean.parseBoolean(rs.getString("SERVICE_ENABLE"));
                String resourcePath = rs.getString("RESOURCE_PATH");
                profiles.add(lifecycleProfile(con, enable, resourcePath));
            }
        } catch (CacheableException e) {
          throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSourceProvider.close(rs);
            dataSourceProvider.close(pstmt);
            dataSourceProvider.close(con);
        }

        return profiles;
    }

    private LifecycleProfile lifecycleProfile(Connection con, boolean enable, String resourcePath) {
        DataSourceProvider dataSourceProvider = new DataSourceProvider();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Identifier id = serial(SQLXML, "API-WATCHER", "UMMM_SELECT_OPTIONS");
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                List<String> addreses = Arrays.asList(rs.getString("NOTIFICATION_SERVER_ADDRESS").split(";"));
                List<String> ports = Arrays.asList(rs.getString("NOTIFICATION_SERVER_PORT").split(";"));
                List<String> names = Arrays.asList(rs.getString("NOTIFICATION_SERVER_NAME").split(";"));
                boolean clearAtStart = "true".equals(rs.getString("CLEAR_AT_START"));
                String jobScheduleInitialDelay = rs.getString("JOB_SCHEDULE_INIT_DELAY");
                String jobScheduleExecuteDelay = rs.getString("JOB_SCHEDULE_EXECUTE_DELAY");
                String jobScheduleTimeUnit = rs.getString("JOB_SCHEDULE_TIME_UNIT");

                ILifecycleOption lifecycleOption = schedule(clearAtStart, TimeUnit.valueOf(jobScheduleTimeUnit), Long.parseLong(jobScheduleInitialDelay), Long.parseLong(jobScheduleExecuteDelay));
                WatcherProfile profile = new WatcherProfile(enable, resourcePath, ports, lifecycleOption);
                profile.setAddreses(addreses);
                profile.setNames(names);

                return profile;
            }
        } catch (CacheableException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSourceProvider.close(rs);
            dataSourceProvider.close(pstmt);
        }

        return null;
    }

    private boolean isRunningTarget(WatcherProfile profile) {
        return profile.enable && profile.isActiveProfile();
    }

}
