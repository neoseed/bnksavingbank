/*
 * 프로그램명 : ServerNodeHandler
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 제공자 ServerNodeHandler
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.node.NodeServerCommands;
import com.mosom.common.standalone.node.NodeServerProvider;
import com.mosom.common.standalone.LifecycleServerProvider;
import com.mosom.common.standalone.restful.RequestStructure;
import com.mosom.common.standalone.restful.ResponseModel;

import static com.mosom.common.standalone.restful.ProcessingStatus.FAILURE;
import static com.mosom.common.standalone.restful.ProcessingStatus.SUCCESS;

public class ServerNodeHandler extends BaseProcessHandler {

    /**
     * URI:/{0}/{1}/{2}/watcher[/{parameter(n)}/{value(n)}..*]
     * Example:/core/json/utf8/lifecycle/command/state
     *                                          /reload
     * 0:REQUESTER
     * 1:RESPONSE_TYPE
     * 2:RESPONSE_CHARSET
     * N:PARAMETER(n), VALUE(n)
     */
    @Override
    public ResponseModel execute() {
        try {
            RequestStructure request = model.getRequestStructure();
            NodeServerCommands command = NodeServerCommands.valueOf(request.get("command").toUpperCase());
            NodeServerProvider nodeProvider = NodeServerProvider.instance();
            LifecycleServerProvider currentProvider = LifecycleServerProvider.instance();
            boolean isProcessingStatus;

            switch (command) {
                case STATE:
                    model.addResults(nodeProvider.nodes());
                    break;
                case RELOAD:
                    nodeProvider.reload();
                    model.addResults(nodeProvider.nodes());
                    break;
            }

            isProcessingStatus = !model.getResults().isEmpty();
            model.setProcessingStatus(isProcessingStatus ? SUCCESS : FAILURE);
            model.setProcessingStatusMessage(
                    "ResponseServerID:[" + currentProvider.id() + " | " + currentProvider.server().state() +"]" +
                    ", ResponseServerHealth:[" + currentProvider.server().health() + "]"
            );
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            log().info("{LifecycleHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            log().info("{LifecycleHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        }

        return model;
    }

}
