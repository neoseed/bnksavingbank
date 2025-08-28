/*
 * 프로그램명 : CacheHandler
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 제공자 Cache
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.ILifecycleOption;
import com.mosom.common.standalone.LifecycleException;
import com.mosom.common.standalone.LifecycleServerProvider;
import com.mosom.common.standalone.cache.CacheCommands;
import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.ICacheable;
import com.mosom.common.standalone.cache.Identifier;
import com.mosom.common.standalone.restful.RequestStructure;
import com.mosom.common.standalone.restful.ResponseModel;

import static com.mosom.common.standalone.LifecycleSituations.*;
import static com.mosom.common.standalone.cache.helper.CacheProviderFinder.find;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;
import static com.mosom.common.standalone.restful.ProcessingStatus.FAILURE;
import static com.mosom.common.standalone.restful.ProcessingStatus.SUCCESS;

public class CacheHandler extends BaseProcessHandler {

    /**
     * URI:/{0}/{1}/{2}/cache[/{parameter(n)}/{value(n)}..*]
     * Example:/rpa/json/utf8/cache
     *                             /type/data/name/messagehubtemplate/command/state
     *                             /type/data/name/messagehubtemplate/command/list
     *                             /type/data/name/messagehubtemplate/command/one/key/{key}
     *                             /type/data/name/kkobizmessagetemplate/command/state
     *                             /type/data/name/kkobizmessagetemplate/command/list
     *                             /type/data/name/kkobizmessagetemplate/command/one/key/{key}
     *                             /type/document/name/sqlxml/command/state
     *                             /type/document/name/sqlxml/command/list
     *                             /type/document/name/sqlxml/command/one/key/{key}
     * 0:REQUESTER
     * 1:RESPONSE_TYPE
     * 2:RESPONSE_CHARSET
     * N:PARAMETER(n), VALUE(n)
     */
    @Override
    public ResponseModel execute() {
        try {
            RequestStructure request = model.getRequestStructure();
            String type = request.get("type").toUpperCase();
            String name = request.get("name").toUpperCase();
            ICacheable<?> cache = find(type, name);
            CacheCommands command = CacheCommands.valueOf(request.get("command").toUpperCase());
            ILifecycleOption option = cache.option();
            LifecycleServerProvider provider = LifecycleServerProvider.instance();
            boolean isProcessingStatus = false;

            switch (command) {
                case STATE:
                    isProcessingStatus = true;
                    break;
                case INITIALIZE:
                    cache.initialize(generateOption(request));
                    isProcessingStatus = cache.situation() == INITIALIZE;
                    break;
                case START:
                    cache.start();
                    isProcessingStatus = cache.situation() == RUNNING;
                    break;
                case SHUTDOWN:
                    cache.shutdown();
                    isProcessingStatus = cache.situation() == SHUTDOWN;
                    break;
                case RELOAD:
                    if (request.isParameterContainsKey("key")) {
                        cache.reload(serial(cache.name(), request.get("key")));
                    } else {
                        cache.reload();
                    }

                    isProcessingStatus = cache.situation() == RUNNING;
                    break;
                case LIST:
                    if (request.isParameterContainsKey("key")) {
                        Identifier group = serial(cache.name(), request.get("key"));

                        for (Object o : cache.list(group)) {
                            model.addResults(o);
                        }
                    } else {
                        for (Object o : cache.list()) {
                            model.addResults(o);
                        }
                    }

                    isProcessingStatus = model.isNotEmpty();
                    break;
                case ONE:
                    if (!request.isParameterContainsKey("key")) {
                        throw new CacheableException("['KEY'] parameter must be included.");
                    }

                    Identifier key = serial(cache.name(), request.get("key"));
                    Object o = cache.one(key);
                    isProcessingStatus = o != null;

                    if (isProcessingStatus) {
                        model.setIdentifier(key.accessor());
                        model.addResults(o);
                    }

                    break;
                case LOADSTATIC:
                    isProcessingStatus = true;
                    cache.loadstatic();
                    break;
            }

            model.setProcessingStatus(isProcessingStatus ? SUCCESS : FAILURE);
            model.setProcessingStatusMessage(
                    "Response-Server-ID:[" + provider.id() + " | " + provider.server().state() + "]" +
                    ", Response-Server-Health:[" + provider.server().health() + "]" +
                    ", Cache-Situation:[" + cache.situation().name() + "]" +
                    ", Cache-Size:[" + cache.size() + "]" +
                    ", Cache-UpdateTime:[" + cache.time() + "]" +
                    ", Cache-Option:[" + (option != null ? option.toString() : "Not mapping.") + "]"
            );
        } catch (CacheableException e) {
            e.printStackTrace();
            log().info("{CacheHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        } catch (LifecycleException e) {
            e.printStackTrace();
            log().info("{CacheHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            log().info("{CacheHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            log().info("{CacheHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        }

        return model;
    }

}
