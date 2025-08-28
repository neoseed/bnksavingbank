/*
 * 프로그램명 : SQLXmlFileAccessor
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스의 XML File 접근자
 */
package com.mosom.common.standalone.cache.document;

import com.cv.jff.common.util.XMLUtil;
import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.Identifier;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.mosom.common.standalone.cache.document.DocumentCacheTypeNames.SQLXML;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;

public class SQLXmlFileAccessor {

    private static final String SQL_XML_DIRECTORY = ResourceBundle.getBundle("JFFInstall").getString("query.dir");

    private static final File SQL_DIRECTORY_ACCESSOR = new File(SQL_XML_DIRECTORY);

    private static final FilenameFilter XML_EXTENSTION_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File directory, String name) {
            return name.toUpperCase().contains(XML_EXTENSTION.toUpperCase());
        }
    };

    private static final ThreadLocal<SimpleDateFormat> LAST_MODIFIED_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };

    private static final String XML_EXTENSTION = ".xml";

    private static final String NODE_NAME = "NAME";

    private static final String NODE_DESCRIPTION = "DESC";

    private static final String NODE_PROGRAM = "PROG";

    private static final String NODE_STATEMENT = "STATEMENT";

    private static final String NODE_CDATASECTION = "#CDATA-SECTION";

    public static List<String> getFilenames() {
        String[] list = SQL_DIRECTORY_ACCESSOR.list(XML_EXTENSTION_FILTER);

        if (list != null) {
            return Arrays.asList(list);
        }

        return Collections.emptyList();
    }

    public static boolean contains(Identifier key) {
        List<String> filenames = getFilenames();
        String group = key.place(1);

        return filenames.contains(group + XML_EXTENSTION);
    }

    public static String getLastModifiedDate(String filename) {
        return LAST_MODIFIED_DATE_FORMAT.get().format(new File(SQL_XML_DIRECTORY + filename).lastModified());
    }

    public static List<String> getDifferentLastModifiedDate(Map<String, String> map) {
        List<String> differents = new ArrayList<String>();

        for (String filename : getFilenames()) {
            String source = map.get(filename);
            String compare = getLastModifiedDate(filename);

            if (source == null || !source.equals(compare)) {
                differents.add(filename);
            }
        }

        return differents;
    }

    public static List<ImmutableSQLXml> read(String filename) throws CacheableException {
        List<ImmutableSQLXml> models = new ArrayList<ImmutableSQLXml>();
        String group = group(filename);
        Element root = root(filename);
        NodeList list = root.getChildNodes();

        for (int nodeIndex = 0; nodeIndex < list.getLength(); nodeIndex++) {
            Node node = list.item(nodeIndex);
            NamedNodeMap attributes = node.getAttributes();

            if (attributes != null) {
                for (int attrIndex = 0; attrIndex < attributes.getLength(); attrIndex++) {
                    Node attribute = attributes.item(attrIndex);

                    if (NODE_NAME.equalsIgnoreCase(attribute.getNodeName())) {
                        models.add(build(group, node, attribute));
                    }
                }
            }
        }

        return models;
    }

    public static ImmutableSQLXml read(String filename, String queryKey) throws CacheableException {
        String group = group(filename);
        Element root = root(filename);
        NodeList list = root.getChildNodes();

        for (int nodeIndex = 0; nodeIndex < list.getLength(); nodeIndex++) {
            Node node = list.item(nodeIndex);
            NamedNodeMap attributes = node.getAttributes();

            if (attributes != null) {
                for (int attrIndex = 0; attrIndex < attributes.getLength(); attrIndex++) {
                    Node attribute = attributes.item(attrIndex);

                    if (NODE_NAME.equalsIgnoreCase(attribute.getNodeName()) && queryKey.equals(attribute.getNodeValue())) {
                        return build(group, node, attribute);
                    }
                }
            }
        }

        return null;
    }

    private static Element root(String filename) {
        return XMLUtil.loadDocument("file:" + SQL_XML_DIRECTORY + filename);
    }

    private static String group(String filename) {
        return filename.substring(0, filename.lastIndexOf("."));
    }

    private static ImmutableSQLXml build(String group, Node parent, Node attribute) throws CacheableException {
        SQLXml model = new SQLXml();
        model.setIdentifier(serial(SQLXML, group, attribute.getNodeValue()));
        model.setGroup(group);
        model.setName(attribute.getNodeValue());
        model.setComments(findTextContent(parent, NODE_DESCRIPTION));
        model.setProgramCode(findTextContent(parent, NODE_PROGRAM));
        model.setStatement(findStatement(parent));
        model.setTime(System.currentTimeMillis());
        return model;
    }

    private static String findTextContent(Node node, String name) {
        if (node != null) {
            NodeList nodes = node.getChildNodes();

            for (int index = 0; index < nodes.getLength(); index++) {
                Node child = nodes.item(index);

                if (name.equalsIgnoreCase(child.getNodeName())) {
                    return child.getTextContent();
                }
            }
        }

        return "";
    }

    private static String findStatement(Node node) {
        if (node != null) {
            NodeList nodes = node.getChildNodes();

            for (int index = 0; index < nodes.getLength(); index++) {
                Node child = nodes.item(index);

                if (NODE_STATEMENT.equalsIgnoreCase(child.getNodeName())) {
                    return findStatement(child);
                }

                if (NODE_CDATASECTION.equalsIgnoreCase(child.getNodeName())) {
                    return child.getNodeValue();
                }
            }
        }

        return "";
    }

}
