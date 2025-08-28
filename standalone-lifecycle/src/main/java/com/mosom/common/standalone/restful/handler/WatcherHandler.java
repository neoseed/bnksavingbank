/*
 * 프로그램명 : WatcherHandler
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 제공자 Watcher
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.ILifecycleOption;
import com.mosom.common.standalone.LifecycleException;
import com.mosom.common.standalone.LifecycleServerProvider;
import com.mosom.common.standalone.restful.RequestStructure;
import com.mosom.common.standalone.restful.ResponseModel;
import com.mosom.common.standalone.watcher.IWatchable;
import com.mosom.common.standalone.watcher.WatcherCommands;
import com.mosom.common.standalone.watcher.WatcherException;

import static com.mosom.common.standalone.LifecycleSituations.*;
import static com.mosom.common.standalone.restful.ProcessingStatus.FAILURE;
import static com.mosom.common.standalone.restful.ProcessingStatus.SUCCESS;
import static com.mosom.common.standalone.watcher.helper.WatcherProviderFinder.find;

public class WatcherHandler extends BaseProcessHandler {

    /**
     * URI:/{0}/{1}/{2}/watcher[/{parameter(n)}/{value(n)}..*]
     * Example:/core/json/utf8/watcher
     *                               /type/monitor/name/ummmonitor/command/state
     *                               /type/monitor/name/ummmonitor/command/start
     *                               /type/monitor/name/ummmonitor/command/shutdown
     *                               /type/scheduler/name/ummproxysender/command/state
     *                               /type/scheduler/name/ummproxysender/command/start
     *                               /type/scheduler/name/ummproxysender/command/shutdown
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
            IWatchable<?> watcher = find(type, name);
            WatcherCommands command = WatcherCommands.valueOf(request.get("command").toUpperCase());
            ILifecycleOption option = watcher.option();
            LifecycleServerProvider provider = LifecycleServerProvider.instance();
            boolean isProcessingStatus = false;

            switch (command) {
                case STATE:
                    isProcessingStatus = true;
                    break;
                case INITIALIZE:
                    watcher.initialize(generateOption(request));
                    isProcessingStatus = watcher.situation() == INITIALIZE;
                    break;
                case START:
                    watcher.start();
                    isProcessingStatus = watcher.situation() == RUNNING;
                    break;
                case SHUTDOWN:
                    watcher.shutdown();
                    isProcessingStatus = watcher.situation() == SHUTDOWN;
                    break;
            }

            model.addResults(watcher.object());
            model.setProcessingStatus(isProcessingStatus ? SUCCESS : FAILURE);
            model.setProcessingStatusMessage(
                    "ResponseServerID:[" + provider.id() + ":" + provider.server().state() +"]" +
                    ", ResponseServerHealth:[" + provider.server().health() + "]" +
                    ", Watcher-Situation:[" + watcher.situation().name() + "]" +
                    ", Watcher-UpdateTime:[" + watcher.time() + "]" +
                    ", Watcher-Option:[" + (option != null ? option.toString() : "Not mapping.") + "]"
            );
        } catch (WatcherException e) {
            e.printStackTrace();
            log().info("{WatcherHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        } catch (LifecycleException e) {
            e.printStackTrace();
            log().info("{WatcherHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            log().info("{WatcherHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            log().info("{WatcherHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        }

        return model;
    }

}
