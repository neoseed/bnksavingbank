/*
 * 프로그램명 : RESTfulStandaloneServlet
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - Servlet
 */
package com.mosom.common.standalone.servlet;

import com.mosom.common.standalone.restful.RequestStructure;
import com.mosom.common.standalone.restful.ResponseCharacterSets;
import com.mosom.common.standalone.restful.ResponseModel;
import com.mosom.common.standalone.restful.ResponseTypes;
import com.mosom.common.standalone.restful.handler.ResponseProviderGateway;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class RESTfulStandaloneServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        doProcess(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        doProcess(request, response);
    }

    private void doProcess(HttpServletRequest request, HttpServletResponse response) {
        doResponse(response, new ResponseProviderGateway(request).execute());
    }

    private void doResponse(HttpServletResponse response, ResponseModel model) {
        switch (model.getRequestStructure().getResponseType()) {
            case TEXT:
                doResponseText(response, model, false);
                break;
            case HTML:
                model.setProcessingStatusMessage("[HTML] is not support.");
                doResponseText(response, model, true);
                break;
            case XML:
                doResponseXML(response, model);
                break;
            case JSON:
                doResponseJSON(response, model);
                break;
            case OBJECT:
                doResponseObject(response, model);
                break;
            case STREAM:
            case PDF:
            case GIF:
            case JPEG:
            case PNG:
            case SVG:
                model.setProcessingStatusMessage("[Binary] is not support.");
                doResponseText(response, model, true);
                break;
        }
    }

    private void doResponseHeader(HttpServletResponse response, ResponseModel model) {
        RequestStructure request = model.getRequestStructure();
        ResponseTypes responseType = request.getResponseType();
        ResponseCharacterSets responseCharacterSet = request.getResponseCharacterSet();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(contentTypeFor(responseType, responseCharacterSet));
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private String contentTypeFor(ResponseTypes responseType, ResponseCharacterSets responseCharacterSet) {
        switch (responseType) {
            case TEXT:
            case HTML:
            case XML:
            case JSON:
                return responseType.header() + ";charset=" + responseCharacterSet.header();
            default:
                return responseType.header();
        }
    }

    private void doResponseText(HttpServletResponse response, ResponseModel model, boolean force) {
        try {
            if (force) {
                ResponseCharacterSets responseCharacterSet = model.getRequestStructure().getResponseCharacterSet();
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/plain;charset=" + responseCharacterSet.header());
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
            } else {
                doResponseHeader(response, model);
            }

            PrintWriter writer = response.getWriter();
            RequestStructure request = model.getRequestStructure();
            writer.println("requestStructure.requester:" + request.getRequester());
            writer.println("requestStructure.responseType:" + request.getResponseType());
            writer.println("requestStructure.responseCharacterSet:" + request.getResponseCharacterSet());
            writer.println("requestStructure.responseProvider:" + request.getResponseProvider());
            writer.println("requestStructure.parameters:" + request.getParameters());
            writer.println("processingStatus:" + model.getProcessingStatus());
            writer.println("processingStatusMessage:" + model.getProcessingStatusMessage());
            writer.println("identifier:" + model.getIdentifier());
            writer.println("results:" + model.getResults());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doResponseXML(HttpServletResponse response, ResponseModel model) {
        try {
            doResponseHeader(response, model);
            ObjectMapperSerializationSupport.WRITER_XML_PRETTY.writeValue(response.getWriter(), model);
            response.getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doResponseJSON(HttpServletResponse response, ResponseModel model) {
        try {
            doResponseHeader(response, model);
            ObjectMapperSerializationSupport.WRITER_JSON_PRETTY.writeValue(response.getWriter(), model);
            response.getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doResponseObject(HttpServletResponse response, ResponseModel model) {
        try {
            doResponseHeader(response, model);
            ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
            oos.writeObject(model);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
