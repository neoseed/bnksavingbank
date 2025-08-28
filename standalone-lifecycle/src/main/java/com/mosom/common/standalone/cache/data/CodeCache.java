/*
 * 프로그램명 : CodeCache
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스의 구현체
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.*;
import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.ISelfManageable;
import com.mosom.common.standalone.cache.Identifier;
import com.mosom.common.standalone.cache.ScheduleCache;
import com.mosom.common.standalone.cache.document.SQLXmlCache;
import com.mosom.common.standalone.LifecycleServerProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import static com.mosom.common.standalone.LifecycleSituations.*;
import static com.mosom.common.standalone.cache.data.DataCacheTypeNames.CODE;
import static com.mosom.common.standalone.cache.document.DocumentCacheTypeNames.SQLXML;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;

public class CodeCache extends ScheduleCache<ImmutableCode> implements ISelfManageable<ImmutableCode> {

    private final ConcurrentMap<Identifier, ImmutableCode> cache;

    private final ConcurrentMap<Identifier, String> self;

    private final DataSourceProvider dataSourceProvider;

    private final Date time = new Date();

    private boolean isStaticOnce;

    private static class CodeCacheHolder {

        private static final CodeCache INSTANCE = new CodeCache();

    }

    private CodeCache() {
        cache = new ConcurrentHashMap<Identifier, ImmutableCode>();
        self = new ConcurrentHashMap<Identifier, String>();
        dataSourceProvider = new DataSourceProvider();
    }

    public static CodeCache instance() {
        return CodeCacheHolder.INSTANCE;
    }

    @Override
    public void initialize(ILifecycleOption option) throws LifecycleException {
        if (isSituation(INITIALIZE, STARTING, RUNNING, RELOADING)) {
            log().info("{CodeCache} is not shutdown situation.");
            return;
        }

        if (isSituation(SHUTDOWN)) {
            super.initialize(option);
            log().info("{CodeCache} is initialize.");
        }
    }

    @Override
    public void start() throws CacheableException {
        if (isSituation(STARTING, RUNNING, RELOADING)) {
            log().info("{CodeCache} is already running situation.");
            return;
        }

        if (isSituation(SHUTDOWN)) {
            log().info("{CodeCache} is not initialize.");
            return;
        }

        if (isSituation(INITIALIZE)) {
            super.start();
            log().info("{CodeCache} is running.");
        }
    }

    @Override
    public void shutdown() {
        if (isStaticOnce) {
            clear();
            isStaticOnce = false;
            log().info("{CodeCache} is off static mode.");
        }

        if (isSituation(SHUTDOWN)) {
            log().info("{CodeCache} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{CodeCache} is already starting situation.");
            return;
        }

        if (isSituation(RELOADING)) {
            log().info("{CodeCache} is already reloading situation.");
            return;
        }

        if (isSituation(INITIALIZE, RUNNING)) {
            super.shutdown();
            log().info("{CodeCache} is shutdown.");
        }
    }

    @Override
    public void loadstatic() {
        if (isSituation(STARTING, RUNNING, RELOADING)) {
            log().info("{CodeCache} is already running situation.");
            return;
        }

        if (isStaticOnce) {
            log().info("{CodeCache} is already on static mode.");
            return;
        }

        isStaticOnce = true;
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        load();
                    } catch (CacheableException e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    try {
                        executor.shutdown();

                        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                            executor.shutdownNow();
                            log().error("{CodeCache} Executor shutdown timed out.");
                        }
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                        log().error("{CodeCache} Executor shutdown interrupted.", e);
                    }
                }
            }
        });
        log().info("{CodeCache} is initialize on static mode.");
    }

    @Override
    public void reload() throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{CodeCache} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{CodeCache} is already starting situation.");
            return;
        }

        if (isSituation(RELOADING)) {
            log().info("{CodeCache} is already reloading situation.");
            return;
        }

        if (isSituation(RUNNING)) {
            log().info("{CodeCache} is reloading...");
            super.reload();
            log().info("{CodeCache} is running.");
        }
    }

    @Override
    public void reload(Identifier key) throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{CodeCache} is not running situation.");
            return;
        }

        if (isSituation(STARTING)) {
            log().info("{CodeCache} is already starting situation.");
            return;
        }

        if (isSituation(RELOADING)) {
            log().info("{CodeCache} is already reloading situation.");
            return;
        }

        if (isSituation(RUNNING)) {
            log().info("{CodeCache} is reloading...");
            super.reload(key);
            log().info("{CodeCache} is running.");
        }
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public List<ImmutableCode> list() throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{CodeCache} is not running situation.");
        }

        return Collections.unmodifiableList(new ArrayList<ImmutableCode>(cache.values()));
    }

    @Override
    public List<ImmutableCode> list(Identifier group) throws CacheableException {
        if (isSituation(INITIALIZE, SHUTDOWN)) {
            log().info("{CodeCache} is not running situation.");
        }

        List<ImmutableCode> models = new ArrayList<ImmutableCode>();

        OUTER:
        for (Identifier src : cache.keySet()) {
            for (String place : group.places()) {
                int srcorder = src.order(place);
                int compareorder = group.order(place);

                if (srcorder != compareorder) {
                    continue OUTER;
                }
            }

            models.add(cache.get(src));
        }

        if (models.isEmpty()) {
            return groups(group);
        }

        return Collections.unmodifiableList(models);
    }

    @Override
    public ImmutableCode one(Identifier key) throws CacheableException {
        ImmutableCode model = cache.get(key);

        if (model == null) {
            load(key);
            return cache.get(key);
        }

        return model;
    }

    /**
     * DmdUtil.getCode(DmdConnection con, String key1, String key2, String option) 대응
     */
    public String value(String key1, String key2, String option) throws CacheableException {
        return value(key1, key2, Integer.parseInt(option));
    }

    public String value(String key1, String key2, int option) throws CacheableException {
        return value(serial(CODE, key1, key2), option);
    }

    public String value(Identifier key, int option) throws CacheableException {
        ImmutableCode model = one(key);

        try {
            switch (option) {
                case 1:
                    return model.getComment1();
                case 2:
                    return model.getComment2();
                case 3:
                    return model.getComment3();
                case 4:
                    return model.getOption1();
                case 5:
                    return model.getOption2();
                case 6:
                    return model.getOption3();
                case 7:
                    return model.getOption4();
                case 8:
                    return model.getOption5();
                case 9:
                    return model.getOption6();
                default:
                    throw new CacheableException("Option[" + option + "] not found.");
            }
        } catch (NumberFormatException e) {
            return model.getComment1();
        }
    }

    @Override
    public void set(List<ImmutableCode> models) {
        if (isSituation(STARTING) || cache.isEmpty()) {
            Map<Identifier, ImmutableCode> map = new HashMap<Identifier, ImmutableCode>(models.size());

            for (ImmutableCode model : models) {
                map.put(model.getIdentifier(), model);
            }

            cache.putAll(map);
        } else {
            for (ImmutableCode model : models) {
                set(model, true);
            }
        }
    }

    @Override
    public void set(ImmutableCode model, boolean onlyIfAbsent) {
        if (onlyIfAbsent) {
            cache.putIfAbsent(model.getIdentifier(), model);
        } else {
            cache.put(model.getIdentifier(), model);
        }
    }

    @Override
    public void remove(ImmutableCode model) {
        cache.remove(model.getIdentifier());
    }

    @Override
    public Date time() {
        return time;
    }

    @Override
    public DataCacheTypeNames name() {
        return CODE;
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
            Identifier id = serial(SQLXML, "API-CACHE", "SELECT_ALL_CODE");
            Connection con = dataSourceProvider.getConnection(true);
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                List<ImmutableCode> models = new ArrayList<ImmutableCode>();

                do {
                    models.add(build(rs));
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
            Identifier id = serial(SQLXML, "API-CACHE", "SELECT_CODE");
            Connection con = dataSourceProvider.getConnection(true);
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());
            pstmt.setString(1, key.place(1));
            pstmt.setString(2, key.place(2));
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new CacheableException("Code[" + key.accessor() + "] not found.");
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

    private List<ImmutableCode> groups(Identifier key) throws CacheableException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ImmutableCode> models = new ArrayList<ImmutableCode>();

        try {
            Identifier id = serial(SQLXML, "API-CACHE", "SELECT_CODE_GROUP");
            Connection con = dataSourceProvider.getConnection(true);
            pstmt = con.prepareStatement(SQLXmlCache.instance().one(id).getStatement());

            for (int order = 1; order <= key.group(); order++) {
                pstmt.setString(order, key.place(order));
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                do {
                    models.add(build(rs));
                } while (rs.next());

                set(models);
            }
        } catch (SQLException e) {
            throw new CacheableException(e);
        } finally {
            time.setTime(System.currentTimeMillis());
            dataSourceProvider.close(rs);
            dataSourceProvider.close(pstmt);
        }

        return Collections.unmodifiableList(models);
    }

    private ImmutableCode build(ResultSet rs) throws CacheableException {
        try {
            Code model = new Code();
            model.setCategory(ImmutableCode.Category.find(rs.getString("ENTITY")));
            model.setKey1(rs.getString("KEY1"));
            model.setKey2(rs.getString("KEY2"));
            model.setOrderSequence(rs.getInt("ORDER_SEQUENCE"));
            model.setComment1(rs.getString("COMMENT1"));
            model.setComment2(rs.getString("COMMENT2"));
            model.setComment3(rs.getString("COMMENT3"));
            model.setOption1(rs.getString("ID1"));
            model.setOption2(rs.getString("ID2"));
            model.setOption3(rs.getString("ID3"));
            model.setOption4(rs.getString("ID4"));
            model.setOption5(rs.getString("ID5"));
            model.setOption6(rs.getString("ID6"));
            model.setIdentifier(serial(name(), model.getKey1(), model.getKey2()));
            return model;
        } catch (SQLException e) {
            throw new CacheableException(e);
        }
    }

    @Override
    public void storage(ImmutableCode key) {
        self.put(key.getIdentifier(), LifecycleServerProvider.instance().id());
    }

    @Override
    public boolean isStorage(ImmutableCode key) {
        return LifecycleServerProvider.instance().id().equals(self.get(key.getIdentifier()));
    }

    @Override
    public void unstorage(ImmutableCode key) {
        self.remove(key.getIdentifier());
    }

}
