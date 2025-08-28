/*
 * 프로그램명 : ResponseModel
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 Data Model
 */
package com.mosom.common.standalone.restful;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private RequestStructure requestStructure;

    private ProcessingStatus processingStatus;

    private String processingStatusMessage;

    private Object identifier;

    private Map<String, Object> results;

    public ResponseModel() {
        setRequestStructure(new RequestStructure());
        setProcessingStatus(ProcessingStatus.FAILURE);
        setProcessingStatusMessage("");
        setIdentifier("");
        setResults(new LinkedHashMap<String, Object>());
    }

    public ResponseModel(String processingStatusMessage) {
        this();
        setProcessingStatusMessage(processingStatusMessage);
    }

    public RequestStructure getRequestStructure() {
        return requestStructure;
    }

    public void setRequestStructure(RequestStructure requestStructure) {
        this.requestStructure = requestStructure;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getProcessingStatusMessage() {
        return processingStatusMessage;
    }

    public void setProcessingStatusMessage(String processingStatusMessage) {
        this.processingStatusMessage = processingStatusMessage;
    }

    public Object getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Object identifier) {
        this.identifier = identifier;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }

    public void addResults(Object result) {
        results.put("result" + (results.size() + 1), result);
    }

    public void addResults(String key, Object result) {
        results.put(key, result);
    }

    public boolean isNotEmpty() {
        return !results.isEmpty();
    }

    @Override
    public String toString() {
        return "ResponseModel{" +
                "requestStructure=" + requestStructure +
                ", processingStatus=" + processingStatus +
                ", processingStatusMessage='" + processingStatusMessage + '\'' +
                ", identifier='" + identifier + '\'' +
                ", results=" + results +
                '}';
    }

}
