/*
 * 프로그램명 : MessageHubTemplateCache
 * 설　계　자 : Thomas Parker(임예준) - (2025.07.09)
 * 작　성　자 : Thomas Parker(임예준) - (2025.07.09)
 * 적　　　요 : 독립 Cache 서비스의 구현체
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.DataSourceProvider;
import com.mosom.common.standalone.ILifecycleOption;
import com.mosom.common.standalone.LifecycleException;
import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.IOperationalOption;
import com.mosom.common.standalone.cache.Identifier;
import com.mosom.common.standalone.cache.OperationalCache;
import com.mosom.common.standalone.cache.document.SQLXmlCache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import static com.mosom.common.standalone.LifecycleSituations.*;
import static com.mosom.common.standalone.cache.data.DataCacheTypeNames.MESSAGEHUBTEMPLATE;
import static com.mosom.common.standalone.cache.document.DocumentCacheTypeNames.SQLXML;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;

public class MessageHubTemplateCache extends OperationalCache<ImmutableMessageHubTemplate> {

    private final ConcurrentMap<Identifier, ImmutableMessageHubTemplate> cache;

    private final DataSourceProvider dataSourceProvider;

    private final Date time = new Date();

    private boolean isStaticOnce;

    private static class MessageHubTemplateCacheHolder {

        private static final MessageHubTemplateCache INSTANCE = new MessageHubTemplateCache();

    }

    private MessageHubTemplateCache() {
        cache = new ConcurrentHashMap<Identifier, ImmutableMessageHubTemplate>();
        dataSourceProvider = new DataSourceProvider();
    }

    public static MessageHubTemplateCache instance() {
        return MessageHubTemplateCacheHolder.INSTANCE;
    }

    @Override
    public void initialize(ILifecycleOption option) throws LifecycleException {
        if (isSituation(INITIALIZE, STARTING, RUNNING, RELOADING)) {
            log().info("{MessageHubTemplateCache} is not shutdown situation.");
            return;
        }

        if (isSituation(SHUTDOWN)) {
            super.initialize(option);
            log().info("{MessageHubTemplateCache} is initialize.");
        }
    }

    @Override
    public void start() throws LifecycleException {
        if (isSituation(STARTING, RUNNING, RELOADING)) {
            log().info("{MessageHubTemplateCache} is already running situation.");
            return;
        }

        if (isSituation(SHUTDOWN)) {
            log().info("{MessageHubTemplateCache} is not initialize.");
            return;
        }

        if (isSituation(INITIALIZE)) {
            super.start();
            log().info("{MessageHubTemplateCache} is running.");
        }
    }

    @Override
    public void shutdown() {
        if (isStaticOnce) {
            clear();
            isStaticOnce = false;
            log().info("{MessageHubTemplateCache} is off static mode.");
        }

        if (isSituation(SHUTDOWN)) {
            log().info("{MessageHubTemplateCache} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{MessageHubTemplateCache} is already starting situation.");
            return;
        }

        if (isSituation(RELOADING)) {
            log().info("{MessageHubTemplateCache} is already starting situation.");
            return;
        }

        if (isSituation(INITIALIZE, RUNNING)) {
            super.shutdown();
            log().info("{MessageHubTemplateCache} is shutdown.");
        }
    }

    @Override
    public void loadstatic() {
        if (isSituation(STARTING, RUNNING, RELOADING)) {
            log().info("{MessageHubTemplateCache} is already running situation.");
            return;
        }

        if (isStaticOnce) {
            log().info("{MessageHubTemplateCache} is already on static mode.");
            return;
        }

        isStaticOnce = true;
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    load();
                } finally {
                    try {
                        executor.shutdown();

                        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                            executor.shutdownNow();
                            log().error("{MessageHubTemplateCache} Executor shutdown timed out.");
                        }
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                        log().error("{MessageHubTemplateCache} Executor shutdown interrupted.", e);
                    }
                }
            }
        });
        log().info("{MessageHubTemplateCache} is initialize on static mode.");
    }

    @Override
    public void reload() throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{MessageHubTemplateCache} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{MessageHubTemplateCache} is already starting situation.");
            return;
        }

        if (isSituation(RELOADING)) {
            log().info("{MessageHubTemplateCache} is already reloading situation.");
            return;
        }

        if (isSituation(RUNNING)) {
            log().info("{MessageHubTemplateCache} is reloading...");
            super.reload();
            log().info("{MessageHubTemplateCache} is running.");
        }
    }

    @Override
    public void reload(Identifier key) throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{MessageHubTemplateCache} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{MessageHubTemplateCache} is already starting situation.");
            return;
        }

        if (isSituation(RELOADING)) {
            log().info("{MessageHubTemplateCache} is already reloading situation.");
            return;
        }

        if (isSituation(RUNNING)) {
            log().info("{MessageHubTemplateCache} is reloading...");
            super.reload(key);
            log().info("{MessageHubTemplateCache} is running.");
        }
    }

    @Override
    public IOperationalOption option() {
        return option;
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public List<ImmutableMessageHubTemplate> list() throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{MessageHubTemplateCache} is not running situation.");
        }

        return Collections.unmodifiableList(new ArrayList<ImmutableMessageHubTemplate>(cache.values()));
    }

    @Override
    public List<ImmutableMessageHubTemplate> list(Identifier group) throws CacheableException {
        throw new CacheableException("MessageHubTemplateCache does not support group.");
    }

    @Override
    public ImmutableMessageHubTemplate one(Identifier key) throws CacheableException {
        ImmutableMessageHubTemplate model = cache.get(key);

        if (model == null) {
            load(key);
            return cache.get(key);
        }

        return model;
    }

    @Override
    public void set(List<ImmutableMessageHubTemplate> models) {
        if (isSituation(STARTING) || cache.isEmpty()) {
            Map<Identifier, ImmutableMessageHubTemplate> map = new HashMap<Identifier, ImmutableMessageHubTemplate>(models.size());

            for (ImmutableMessageHubTemplate model : models) {
                map.put(model.getIdentifier(), model);
            }

            cache.putAll(map);
        } else {
            for (ImmutableMessageHubTemplate model : models) {
                set(model, true);
            }
        }
    }

    @Override
    public void set(ImmutableMessageHubTemplate model, boolean onlyIfAbsent) {
        if (onlyIfAbsent) {
            cache.putIfAbsent(model.getIdentifier(), model);
        } else {
            cache.put(model.getIdentifier(), model);
        }
    }

    @Override
    public void remove(ImmutableMessageHubTemplate model) {
        cache.remove(model.getIdentifier());
    }

    @Override
    public Date time() {
        return time;
    }

    @Override
    public DataCacheTypeNames name() {
        return MESSAGEHUBTEMPLATE;
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public void load() {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Identifier id = serial(SQLXML, "API-CACHE", "SELECT_ALL_MESSAGEHUB_TEMPLATE");
            Connection con = dataSourceProvider.getConnection(true);
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                List<ImmutableMessageHubTemplate> models = new ArrayList<ImmutableMessageHubTemplate>();

                do {
                    ImmutableMessageHubTemplate model = build(rs);
                    models.add(model);
                } while (rs.next());

                set(models);
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

    @Override
    public void load(Identifier key) throws CacheableException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Identifier id = serial(SQLXML, "API-CACHE", "SELECT_MESSAGEHUB_TEMPLATE");
            Connection con = dataSourceProvider.getConnection(true);
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
            pstmt.setString(1, key.accessor().toString());
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new CacheableException("MessageHubCode[" + key.accessor() + "] not found.");
            }

            set(build(rs), false);
        } catch (SQLException e) {
            throw new CacheableException(e);
        } finally {
            time.setTime(System.currentTimeMillis());
            dataSourceProvider.close(rs);
            dataSourceProvider.close(pstmt);
        }
    }

    private ImmutableMessageHubTemplate build(ResultSet rs) throws CacheableException {
        try {
            MessageHubTemplate model = new MessageHubTemplate();
            model.setCode(rs.getString("UMS_MSG_TPL_CD"));
            model.setTemplateKey(rs.getString("UMS_MSG_TPL_KEY"));
            model.setType(rs.getString("UMS_MSG_TPL_TYPE"));
            model.setTitle(rs.getString("UMS_MSG_TPL_NAME"));
            model.setMessage(rs.getString("UMS_MSG_TPL_VCONTENTS"));
            model.setCategory(rs.getString("UMS_MSG_TPL_KIND_DESC"));
            model.setActivationOfUser(rs.getString("UMS_MSG_TPL_USE"));
            model.setActivationOfProxyWatcher(rs.getString("UMS_MSG_TPL_USE_PW"));
            model.setChannel(rs.getString("UMS_MSG_TPL_CHANNEL"));
            model.setSendType(rs.getString("UMS_MSG_TPL_SEND_TYPE"));
            model.setIdentifier(serial(name(), model.getCode()));

            return model;
        } catch (SQLException e) {
            throw new CacheableException(e);
        }
    }

}
