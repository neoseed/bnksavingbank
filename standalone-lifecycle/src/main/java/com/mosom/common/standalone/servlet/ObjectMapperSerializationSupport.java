/*
 * ���α׷��� : ObjectMapperSerializationSupport
 * �����衡�� : Thomas Parker(�ӿ���) - (2024.11.08)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2024.11.08)
 * ���������� : �������� �ý��� REST API Service - Servlet Response ����
 */
package com.mosom.common.standalone.servlet;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public final class ObjectMapperSerializationSupport {

    //JSON
    public static final ObjectWriter WRITER_JSON_PRETTY;
    public static final ObjectWriter WRITER_JSON_COMPACT;

    //XML
    public static final ObjectWriter WRITER_XML_PRETTY;
    public static final ObjectWriter WRITER_XML_COMPACT;

    private ObjectMapperSerializationSupport() {

    }

    static {
        ObjectMapper om = new ObjectMapper();
        WRITER_JSON_PRETTY = om.writerWithDefaultPrettyPrinter();
        WRITER_JSON_COMPACT = om.writer();

        XmlMapper xm = new XmlMapper(new WstxInputFactory(), new WstxOutputFactory());
        WRITER_XML_PRETTY = xm.writerWithDefaultPrettyPrinter();
        WRITER_XML_COMPACT = xm.writer();
    }

}
