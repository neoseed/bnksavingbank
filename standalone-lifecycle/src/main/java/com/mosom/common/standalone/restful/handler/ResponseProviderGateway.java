/*
 * 프로그램명 : ResponseProviderGateway
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 제공자 Gateway
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.restful.ProcessException;
import com.mosom.common.standalone.restful.RequestStructure;
import com.mosom.common.standalone.restful.ResponseModel;
import com.mosom.common.standalone.restful.ResponseProviders;
import com.mosom.consparam.LogCons;

import javax.servlet.http.HttpServletRequest;
import java.util.ListIterator;

import static com.dmdlogging.DmdLog.getLog;

public class ResponseProviderGateway extends BaseProcessHandler {

    private final HttpServletRequest request;

    public ResponseProviderGateway(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public ResponseModel execute() {
        ResponseModel model = new ResponseModel();

        try {
            RequestStructure request = model.getRequestStructure();
            request.parse(this.request);
            log().info("<RequestStructure, Remote Address:" + this.request.getRemoteAddr() + ">");
            log().info("[REQUESTURI       ]:" + this.request.getRequestURI().substring(0, this.request.getRequestURI().lastIndexOf("/restservice.ext")));
            log().info("[REQUESTER        ]:" + request.getRequester());
            log().info("[RESPONSE_TYPE    ]:" + request.getResponseType());
            log().info("[RESPONSE_CHARSET ]:" + request.getResponseCharacterSet());
            log().info("[RESPONSE_PROVIDER]:" + request.getResponseProvider());
            log().info("[PARAMETERS       ]:");

            for (ListIterator<String> itr = request.getParameterKeys().listIterator(); itr.hasNext();) {
                String key = itr.next();
                getLog(LogCons.MOSOM_COMMON).info("PARAMETER" + itr.nextIndex() + ":" + key + ":" + request.get(key));
            }

            BaseProcessHandler processHandler = find(request.getResponseProvider());
            processHandler.setResponseModel(model);

            return processHandler.execute();
        } catch (ProcessException e) {
            model.setProcessingStatusMessage(e.getMessage());
            return model;
        }
    }

    private BaseProcessHandler find(ResponseProviders responseProvider) throws ProcessException {
        switch (responseProvider) {
            case SERVERNODE:
                return new ServerNodeHandler();
            case KKOBIZMESSAGE:
                return new KakaoBizMessageHandler();
            case MESSAGEHUB:
                return new MessageHubHandler();
            case CACHE:
                return new CacheHandler();
            case WATCHER:
                return new WatcherHandler();
            case DATABASE:
                return new DatabaseHandler();
            case RESOURCE:
                return new ResourceHandler();
            case SERVICEDOMAIN:
                return new ServiceDomainHandler();
        }

        throw new ProcessException("ProcessHandler not found.");
    }

}
