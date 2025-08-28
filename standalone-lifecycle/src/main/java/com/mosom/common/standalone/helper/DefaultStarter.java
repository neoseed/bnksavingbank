/*
 * 프로그램명 : DefaultStarter
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 독립 Lifecycle 서비스의 실행
 */
package com.mosom.common.standalone.helper;

import com.mosom.common.standalone.*;
import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.Identifier;
import com.mosom.common.standalone.cache.document.SQLXmlCache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mosom.common.standalone.OptionGenerator.operational;
import static com.mosom.common.standalone.OptionGenerator.schedule;
import static com.mosom.common.standalone.cache.document.DocumentCacheTypeNames.SQLXML;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;

public class DefaultStarter implements ILifecycleStarter {

    @Override
    public void run(String group) {
        List<LifecycleProfile> profiles = lifecycleProfiles(group);
        int port = LifecycleServerProvider.instance().server().port();

        for (LifecycleProfile profile : profiles) {
            if (isRunningTarget(profile, String.valueOf(port))) {
                if (profile.option instanceof IScheduleOption) {
                    IScheduleOption o = (IScheduleOption) profile.option;
                    //Schedule Delay 옵션의 경우 인스턴스 서버의 기동포트 끝자리 수 만큼 Delay 시간(Minute) 추가
                    //인스턴스 서버가 각각 동시에 기동될 때 DBMS 부하를 막기 위해 기동 간격 조정
                    //6611 => 1분 Delay 추가
                    //6612 => 2분 Delay 추가
                    //6613 => 3분 Delay 추가
                    //6619 => 9분 Delay 추가
                    long initialDelay = o.initialDelay() + (port % 10);
                    profile.option = schedule(
                            o.isClearAtStart()
                            , o.timeScale()
                            , initialDelay
                            , o.scheduleDelay());
                }

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
                String[] options = rs.getString("START_OPTION").split(";");
                boolean clearAtStart = Boolean.parseBoolean(options[0]);
                List<String> listenPorts = Arrays.asList(options[1].split("-"));
                ILifecycleOption lifecycleOption = lifecycleOption(clearAtStart, options[2], options[3], options[4]);
                profiles.add(new LifecycleProfile(
                        Boolean.parseBoolean(rs.getString("SERVICE_ENABLE"))
                        , rs.getString("RESOURCE_PATH")
                        , listenPorts
                        , lifecycleOption));
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

    private boolean isRunningTarget(LifecycleProfile profile, String listenPort) {
        return profile.enable && profile.listenPorts.contains(listenPort);
    }

    private ILifecycleOption lifecycleOption(boolean clearAtStart, String option1, String option2, String option3) {
        TimeUnit timeScale = "NONE".equals(option1) ? null : TimeUnit.valueOf(option1);

        if (timeScale == null) {
            return operational(clearAtStart);
        } else {
            return schedule(clearAtStart, timeScale, Long.parseLong(option2), Long.parseLong(option3));
        }
    }

}
