/*
 * 프로그램명 : RequestStructure
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - URI 파라미터 구조
 */
package com.mosom.common.standalone.restful;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class RequestStructure implements Serializable {

    private static final long serialVersionUID = 1L;

    private enum HttpMethods {

        DELETE
        , GET
        , HEAD
        , OPTIONS
        , PATCH
        , POST
        , PUT
        , TRACE

    }

    private Requesters requester;

    private ResponseTypes responseType;

    private ResponseCharacterSets responseCharacterSet;

    private ResponseProviders responseProvider;

    private final Map<String, String> parameters;

    public RequestStructure() {
        requester = Requesters.CORE;
        responseType = ResponseTypes.JSON;
        responseCharacterSet = ResponseCharacterSets.UTF8;
        parameters = new LinkedHashMap<String, String>();
    }

    public void parse(HttpServletRequest request) throws ProcessException {
        //URI:/{0}/{1}/{2}/{3}[/{parameter(n)}/{value(n)}..*]/restservice.ext
        //0:REQUESTER
        //1:RESPONSE_TYPE
        //2:RESPONSE_CHARSET
        //3:RESPONSE_PROVIDER
        //N:[{PARAMETER}/{VALUE}]..*
        //요청 첫문자 및 확장명 제거('/'(첫 문자), '/restservice.ext'(확장명))
        String uri = request.getRequestURI();
        String[] elements;

        try {
            elements = uri.substring(1, uri.lastIndexOf("/restservice.ext")).split("/");
        } catch (StringIndexOutOfBoundsException e) {
            throw new ProcessException("Request URI extenstion do not match.");
        }

        if (elements.length < Preconditions.values().length) {
            throw new ProcessException("Preconditions are not met.");
        }

        if (Arrays.asList(elements).contains("")) {
            throw new ProcessException("Empty place holder exists.");
        }

        setPrecondition(0, elements[0].toUpperCase());
        setPrecondition(1, elements[1].toUpperCase());
        setPrecondition(2, elements[2].toUpperCase());
        setPrecondition(3, elements[3].toUpperCase());

        String[] otherConditions = new String[elements.length - Preconditions.values().length];
        System.arraycopy(elements, Preconditions.values().length, otherConditions, 0, otherConditions.length);

        if (otherConditions.length % 2 != 0) {
            throw new ProcessException("Parameters pair are not met.");
        }

        for (ListIterator<String> itr = Arrays.asList(otherConditions).listIterator(); itr.hasNext();) {
            set(itr.next(), itr.next());
        }

        //POST 요청의 경우
        if (HttpMethods.DELETE.name().equalsIgnoreCase(request.getMethod())
                || HttpMethods.PATCH.name().equalsIgnoreCase(request.getMethod())
                || HttpMethods.POST.name().equalsIgnoreCase(request.getMethod())
                || HttpMethods.PUT.name().equalsIgnoreCase(request.getMethod())) {
            String contentType = request.getHeader("Content-Type");

            if (contentType.toLowerCase().contains(ResponseTypes.JSON.header())) {
                try {
                    BufferedReader reader = request.getReader();
                    StringBuilder builder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> data = mapper.readValue(builder.toString(), new TypeReference<Map<String, String>>() {});
                    parameters.putAll(new TreeMap<String, String>(data));
                } catch (IOException e) {
                    throw new ProcessException(e);
                }
            } else {
                @SuppressWarnings("rawtypes")
                Enumeration names = request.getParameterNames();
                Map<String, String> ordered = new TreeMap<String, String>();

                while (names.hasMoreElements()) {
                    Object o = names.nextElement();

                    if (o instanceof String) {
                        String key = (String) o;
                        ordered.put(key, request.getParameter(key));
                    }
                }

                parameters.putAll(ordered);
            }
        }
    }

    private void setPrecondition(int index, String precondition) throws ProcessException {
        try {
            switch (Preconditions.values()[index]) {
                case REQUESTER:
                    requester = Requesters.valueOf(precondition);
                    break;
                case RESPONSE_TYPE:
                    responseType = ResponseTypes.valueOf(precondition);
                    break;
                case RESPONSE_CHARSET:
                    responseCharacterSet = ResponseCharacterSets.valueOf(precondition);
                    break;
                case RESPONSE_PROVIDER:
                    responseProvider = ResponseProviders.valueOf(precondition);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new ProcessException(e);
        }
    }

    public Requesters getRequester() {
        return requester;
    }

    public ResponseTypes getResponseType() {
        return responseType;
    }

    public ResponseCharacterSets getResponseCharacterSet() {
        return responseCharacterSet;
    }

    public ResponseProviders getResponseProvider() {
        return responseProvider;
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @JsonIgnore
    public List<String> getParameterKeys() {
        return Collections.unmodifiableList(new ArrayList<String>(parameters.keySet()));
    }

    public List<String> getParameterContainsCharacter(String character) {
        List<String> results = new ArrayList<String>();

        for (String key : parameters.keySet()) {
            if (key.contains(character)) {
                results.add(key);
            }
        }

        return Collections.unmodifiableList(results);
    }

    public List<String> getParameterContainsValues(String character) {
        List<String> results = new ArrayList<String>();

        for (String key : getParameterContainsCharacter(character)) {
            results.add(get(key));
        }

        return Collections.unmodifiableList(results);
    }

    public boolean isParameterContainsKey(String key) {
        return parameters.containsKey(key);
    }

    public boolean isParameterContainsCharacter(String key) {
        for (String k : parameters.keySet()) {
            if (k.contains(key)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Group Parameter 요소 중 하나라도 존재하지 않으면 false 반환
     */
    public boolean isGroupParameterContainsKey(String... keys) {
        for (String key : keys) {
            if (!parameters.containsKey(key)) {
                return false;
            }
        }

        return true;
    }

    public void set(String key, String value) throws ProcessException {
        try {
            parameters.put(key, URLDecoder.decode(value, responseCharacterSet.header()));
        } catch (UnsupportedEncodingException e) {
            throw new ProcessException(e);
        }
    }

    public void remove(String key) {
        parameters.remove(key);
    }

    public String get(String key) {
        return parameters.get(key);
    }

    @Override
    public String toString() {
        return "RequestStructure{" +
                "requester=" + requester +
                ", responseType=" + responseType +
                ", responseCharacterSet=" + responseCharacterSet +
                ", responseProvider=" + responseProvider +
                ", parameters=" + parameters +
                '}';
    }

}
