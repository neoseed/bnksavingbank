/*
 * 프로그램명 : SQLXmlCache
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스의 구현체
 */
package com.mosom.common.standalone.cache.document;

import com.mosom.common.standalone.ILifecycleOption;
import com.mosom.common.standalone.IOperationalOption;
import com.mosom.common.standalone.LifecycleException;
import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.Identifier;
import com.mosom.common.standalone.cache.OperationalCache;

import java.util.*;
import java.util.concurrent.*;

import static com.mosom.common.standalone.LifecycleSituations.*;
import static com.mosom.common.standalone.cache.document.DocumentCacheTypeNames.SQLXML;

public class SQLXmlCache extends OperationalCache<ImmutableSQLXml> {

    private final ConcurrentMap<Identifier, ImmutableSQLXml> cache;

    private final ConcurrentMap<String, String> lastModified = new ConcurrentHashMap<String, String>();

    private final Date time = new Date();

    private boolean isStaticOnce;

    private static class SQLXmlCacheHolder {

        private static final SQLXmlCache INSTANCE = new SQLXmlCache();

    }

    private SQLXmlCache() {
        cache = new ConcurrentHashMap<Identifier, ImmutableSQLXml>();
    }

    public static SQLXmlCache instance() {
        return SQLXmlCacheHolder.INSTANCE;
    }

    @Override
    public void initialize(ILifecycleOption option) throws LifecycleException {
        if (isSituation(INITIALIZE, STARTING, RUNNING, RELOADING)) {
            log().info("{SQLXmlCache} is not shutdown situation.");
            return;
        }

        if (isSituation(SHUTDOWN)) {
            super.initialize(option);
            log().info("{SQLXmlCache} is initialize.");
        }
    }

    @Override
    public void start() throws LifecycleException {
        if (isSituation(STARTING, RUNNING, RELOADING)) {
            log().info("{SQLXmlCache} is already running situation.");
            return;
        }

        if (isSituation(SHUTDOWN)) {
            log().info("{SQLXmlCache} is not initialize.");
            return;
        }

        if (isSituation(INITIALIZE)) {
            super.start();
            log().info("{SQLXmlCache} is running.");
        }
    }

    @Override
    public void shutdown() {
        if (isStaticOnce) {
            clear();
            isStaticOnce = false;
            log().info("{SQLXmlCache} is off static mode.");
        }

        if (isSituation(SHUTDOWN)) {
            log().info("{SQLXmlCache} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{SQLXmlCache} is already starting situation.");
            return;
        }

        if (isSituation(RELOADING)) {
            log().info("{SQLXmlCache} is reloading...");
            return;
        }

        if (isSituation(INITIALIZE, RUNNING)) {
            super.shutdown();
            log().info("{SQLXmlCache} is shutdown.");
        }
    }

    @Override
    public void loadstatic() {
        if (isSituation(STARTING, RUNNING, RELOADING)) {
            log().info("{SQLXmlCache} is already running situation.");
            return;
        }

        if (isStaticOnce) {
            log().info("{SQLXmlCache} is already on static mode.");
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
                            log().error("{SQLXmlCache} Executor shutdown timed out.");
                        }
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                        log().error("{SQLXmlCache} Executor shutdown interrupted.", e);
                    }
                }
            }
        });
        log().info("{SQLXmlCache} is initialize on static mode.");
    }

    @Override
    public void reload() throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{SQLXmlCache} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{SQLXmlCache} is already starting situation.");
            return;
        }

        if (isSituation(RELOADING)) {
            log().info("{SQLXmlCache} is already reloading situation.");
            return;
        }

        if (isSituation(RUNNING)) {
            log().info("{SQLXmlCache} is reloading...");
            super.reload();
            log().info("{SQLXmlCache} is running.");
        }
    }

    @Override
    public void reload(Identifier key) throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{SQLXmlCache} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{SQLXmlCache} is already starting situation.");
            return;
        }

        if (isSituation(RELOADING)) {
            log().info("{SQLXmlCache} is already reloading situation.");
            return;
        }

        if (isSituation(RUNNING)) {
            log().info("{SQLXmlCache} is reloading...");
            super.reload(key);
            log().info("{SQLXmlCache} is running.");
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
    public List<ImmutableSQLXml> list() throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{SQLXmlCache} is not running situation.");
        }

        return Collections.unmodifiableList(new ArrayList<ImmutableSQLXml>(cache.values()));
    }

    @Override
    public List<ImmutableSQLXml> list(Identifier group) throws CacheableException {
        throw new CacheableException("KakaoBizMessageTemplateCache does not support group.");
    }

    @Override
    public ImmutableSQLXml one(Identifier key) throws CacheableException {
        ImmutableSQLXml model = cache.get(key);

        if (model == null) {
            load(key);
            return cache.get(key);
        }

        return model;
    }

    @Override
    public void set(List<ImmutableSQLXml> models) {
        if (isSituation(STARTING) || cache.isEmpty()) {
            Map<Identifier, ImmutableSQLXml> map = new HashMap<Identifier, ImmutableSQLXml>(models.size());

            for (ImmutableSQLXml model : models) {
                map.put(model.getIdentifier(), model);
            }

            cache.putAll(map);
        } else {
            for (ImmutableSQLXml model : models) {
                set(model, false);
            }
        }
    }

    @Override
    public void set(ImmutableSQLXml model, boolean onlyIfAbsent) {
        if (onlyIfAbsent) {
            cache.putIfAbsent(model.getIdentifier(), model);
        } else {
            cache.put(model.getIdentifier(), model);
        }
    }

    @Override
    public void remove(ImmutableSQLXml model) {
        cache.remove(model.getIdentifier());
    }

    @Override
    public Date time() {
        return time;
    }

    @Override
    public DocumentCacheTypeNames name() {
        return SQLXML;
    }

    @Override
    public void clear() {
        cache.clear();
        lastModified.clear();
    }

    @Override
    public void load() {
        try {
            List<String> differents = SQLXmlFileAccessor.getDifferentLastModifiedDate(lastModified);

            for (String filename : differents) {
                try {
                    loadSQLXml(filename);
                } catch (CacheableException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            time.setTime(System.currentTimeMillis());
        }
    }

    @Override
    public void load(Identifier key) throws CacheableException {
        if (SQLXmlFileAccessor.contains(key)) {
            loadSQLXml(key);
        } else {
            log().info("{SQLXmlCache} Code[" + key.accessor() + "] not found.");
            load();
        }
    }

    private void loadSQLXml(String filename) throws CacheableException {
        set(SQLXmlFileAccessor.read(filename));
        lastModified.put(filename, SQLXmlFileAccessor.getLastModifiedDate(filename));
    }

    private void loadSQLXml(Identifier key) throws CacheableException {
        String filename = key.place(1) + ".xml";
        String queryKey = key.place(2);
        ImmutableSQLXml model = SQLXmlFileAccessor.read(filename, queryKey);

        if (model != null) {
            set(model, false);
            lastModified.put(filename, SQLXmlFileAccessor.getLastModifiedDate(filename));
            log().info("{SQLXmlCache} Load resource:" + filename + ":" + queryKey);
        } else {
            log().info("{SQLXmlCache} Load resource:" + filename + ":" + queryKey + ":NULL");
        }
    }

}
