/*
 * 프로그램명 : KakaoBizMessageTemplateCache
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
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
import static com.mosom.common.standalone.cache.data.DataCacheTypeNames.KKOBIZMESSAGETEMPLATE;
import static com.mosom.common.standalone.cache.document.DocumentCacheTypeNames.SQLXML;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;

public class KakaoBizMessageTemplateCache extends OperationalCache<ImmutableKakaoBizMessageTemplate> {

    private final ConcurrentMap<Identifier, ImmutableKakaoBizMessageTemplate> cache;

    private final DataSourceProvider dataSourceProvider;

    private final Date time = new Date();

    private boolean isStaticOnce;

    private static class KakaoBizMessageTemplateCacheHolder {

        private static final KakaoBizMessageTemplateCache INSTANCE = new KakaoBizMessageTemplateCache();

    }

    private KakaoBizMessageTemplateCache() {
        cache = new ConcurrentHashMap<Identifier, ImmutableKakaoBizMessageTemplate>();
        dataSourceProvider = new DataSourceProvider();
    }

    public static KakaoBizMessageTemplateCache instance() {
        return KakaoBizMessageTemplateCacheHolder.INSTANCE;
    }

    @Override
    public void initialize(ILifecycleOption option) throws LifecycleException {
        if (isSituation(INITIALIZE, STARTING, RUNNING, RELOADING)) {
            log().info("{KakaoBizMessageTemplateCache} is not shutdown situation.");
            return;
        }

        if (isSituation(SHUTDOWN)) {
            super.initialize(option);
            log().info("{KakaoBizMessageTemplateCache} is initialize.");
        }
    }

    @Override
    public void start() throws LifecycleException {
        if (isSituation(STARTING, RUNNING, RELOADING)) {
            log().info("{KakaoBizMessageTemplateCache} is already running situation.");
            return;
        }

        if (isSituation(SHUTDOWN)) {
            log().info("{KakaoBizMessageTemplateCache} is not initialize.");
            return;
        }

        if (isSituation(INITIALIZE)) {
            super.start();
            log().info("{KakaoBizMessageTemplateCache} is running.");
        }
    }

    @Override
    public void shutdown() {
        if (isStaticOnce) {
            clear();
            isStaticOnce = false;
            log().info("{KakaoBizMessageTemplateCache} is off static mode.");
        }

        if (isSituation(SHUTDOWN)) {
            log().info("{KakaoBizMessageTemplateCache} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{KakaoBizMessageTemplateCache} is already starting situation.");
            return;
        }

        if (isSituation(RELOADING)) {
            log().info("{KakaoBizMessageTemplateCache} is already starting situation.");
            return;
        }

        if (isSituation(INITIALIZE, RUNNING)) {
            super.shutdown();
            log().info("{KakaoBizMessageTemplateCache} is shutdown.");
        }
    }

    @Override
    public void loadstatic() {
        if (isSituation(STARTING, RUNNING, RELOADING)) {
            log().info("{KakaoBizMessageTemplateCache} is already running situation.");
            return;
        }

        if (isStaticOnce) {
            log().info("{KakaoBizMessageTemplateCache} is already on static mode.");
            return;
        }

        isStaticOnce = true;
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    load();
                } catch (CacheableException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        executor.shutdown();

                        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                            executor.shutdownNow();
                            log().error("{KakaoBizMessageTemplateCache} Executor shutdown timed out.");
                        }
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                        log().error("{KakaoBizMessageTemplateCache} Executor shutdown interrupted.", e);
                    }
                }
            }
        });
        log().info("{KakaoBizMessageTemplateCache} is initialize on static mode.");
    }

    @Override
    public void reload() throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{KakaoBizMessageTemplateCache} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{KakaoBizMessageTemplateCache} is already starting situation.");
            return;
        }

        if (isSituation(RELOADING)) {
            log().info("{KakaoBizMessageTemplateCache} is already reloading situation.");
            return;
        }

        if (isSituation(RUNNING)) {
            log().info("{KakaoBizMessageTemplateCache} is reloading...");
            super.reload();
            log().info("{KakaoBizMessageTemplateCache} is running.");
        }
    }

    @Override
    public void reload(Identifier key) throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{KakaoBizMessageTemplateCache} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{KakaoBizMessageTemplateCache} is already starting situation.");
            return;
        }

        if (isSituation(RELOADING)) {
            log().info("{KakaoBizMessageTemplateCache} is already reloading situation.");
            return;
        }

        if (isSituation(RUNNING)) {
            log().info("{KakaoBizMessageTemplateCache} is reloading...");
            super.reload(key);
            log().info("{KakaoBizMessageTemplateCache} is running.");
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
    public List<ImmutableKakaoBizMessageTemplate> list() throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{KakaoBizMessageTemplateCache} is not running situation.");
        }

        return Collections.unmodifiableList(new ArrayList<ImmutableKakaoBizMessageTemplate>(cache.values()));
    }

    @Override
    public List<ImmutableKakaoBizMessageTemplate> list(Identifier group) throws CacheableException {
        throw new CacheableException("KakaoBizMessageTemplateCache does not support group.");
    }

    @Override
    public ImmutableKakaoBizMessageTemplate one(Identifier key) throws CacheableException {
        ImmutableKakaoBizMessageTemplate model = cache.get(key);

        if (model == null) {
            load(key);
            return cache.get(key);
        }

        return model;
    }

    @Override
    public void set(List<ImmutableKakaoBizMessageTemplate> models) {
        if (isSituation(STARTING) || cache.isEmpty()) {
            Map<Identifier, ImmutableKakaoBizMessageTemplate> map = new HashMap<Identifier, ImmutableKakaoBizMessageTemplate>(models.size());

            for (ImmutableKakaoBizMessageTemplate model : models) {
                map.put(model.getIdentifier(), model);
            }

            cache.putAll(map);
        } else {
            for (ImmutableKakaoBizMessageTemplate model : models) {
                set(model, true);
            }
        }
    }

    @Override
    public void set(ImmutableKakaoBizMessageTemplate model, boolean onlyIfAbsent) {
        if (onlyIfAbsent) {
            cache.putIfAbsent(model.getIdentifier(), model);
        } else {
            cache.put(model.getIdentifier(), model);
        }
    }

    @Override
    public void remove(ImmutableKakaoBizMessageTemplate model) {
        cache.remove(model.getIdentifier());
    }

    @Override
    public Date time() {
        return time;
    }

    @Override
    public DataCacheTypeNames name() {
        return KKOBIZMESSAGETEMPLATE;
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public void load() throws CacheableException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Identifier id = serial(SQLXML, "API-CACHE", "SELECT_ALL_KKO_TEMPLATE");
            Connection con = dataSourceProvider.getConnection(true);
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                List<ImmutableKakaoBizMessageTemplate> models = new ArrayList<ImmutableKakaoBizMessageTemplate>();

                do {
                    ImmutableKakaoBizMessageTemplate model = build(rs);
                    models.add(model);
                } while (rs.next());

                set(models);
            }
        } catch (CacheableException e) {
            throw new CacheableException(e);
        } catch (SQLException e) {
            throw new CacheableException(e);
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
            Identifier id = serial(SQLXML, "API-CACHE", "SELECT_KKO_TEMPLATE");
            Connection con = dataSourceProvider.getConnection(true);
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
            pstmt.setString(1, key.accessor().toString());
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new CacheableException("KakaoBizMessageCode[" + key.accessor() + "] not found.");
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

    private ImmutableKakaoBizMessageTemplate build(ResultSet rs) throws CacheableException {
        try {
            KakaoBizMessageTemplate model = new KakaoBizMessageTemplate();
            model.setCode(rs.getString("K_MSG_TPL_CD"));
            model.setTitle(rs.getString("K_MSG_TPL_NAME"));
            model.setType(rs.getString("K_MSG_TPL_TYPE"));
            model.setCategory(rs.getString("K_MSG_TPL_KIND"));
            model.setMessage(rs.getString("K_MSG_TPL_VCONTENTS"));
            model.setActivationOfUser(rs.getString("K_MSG_TPL_USE"));
            model.setActivationOfProxyWatcher(rs.getString("K_MSG_TPL_USE_PW"));
            model.setChannel(rs.getString("K_MSG_TPL_CHANNEL"));
            model.setSendType(rs.getString("K_MSG_TPL_SEND_TYPE"));
            model.setIdentifier(serial(name(), model.getCode()));

            return model;
        } catch (SQLException e) {
            throw new CacheableException(e);
        }
    }

}
