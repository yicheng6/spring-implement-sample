package com.yicheng6.beans.factory.support;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;

/**
 * Created by yicheng6 on 15/12/23.
 */
public class XmlBeanDefinitionReader {

    private String location;

    private DefaultListableBeanFactory beanFactory;

    public XmlBeanDefinitionReader(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void loadBeanDefinitions() throws Exception {
        URL url = ClassLoader.getSystemResource(location);
        File xmlFile = new File(url.getFile());
//        System.out.println(xmlFile.canRead());
        try {
            InputStream inputStream = new FileInputStream(xmlFile);
            try {
//                StringBuffer buffer = new StringBuffer();
//                byte[] bytes = new byte[2048];
//                int num = inputStream.read(bytes);
//                for (int i = 0; i < num; i++) {
//                    buffer.append((char) bytes[i]);
//                }
//                System.out.println(buffer.toString());

                InputSource inputSource = new InputSource(inputStream);
                doLoadBeanDefinitions(inputSource, xmlFile);
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            // File toString() return getPath()
            throw new Exception("IOException parsing XML document from " + xmlFile, e);
        }
    }

    private void doLoadBeanDefinitions(InputSource inputSource, File xmlFile) throws Exception {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
            Document document = domBuilder.parse(inputSource);
            registerBeanDefinitions(document);
        } catch (SAXParseException e) {
            throw new Exception("Line " + e.getLineNumber() + " in XML document from " + xmlFile + " is invalid", e);
        } catch (SAXException e) {
            throw new Exception("XML document from " + xmlFile + " is invalid", e);
        } catch (ParserConfigurationException e) {
            throw new Exception("Parser configuration exception parsing XML from " + xmlFile, e);
        } catch (IOException e) {
            throw new Exception("IOException parsing XML from " + xmlFile, e);
        }
    }

    private void registerBeanDefinitions(Document document) {
        BeanDefinitionDocumentReader documentReader = new BeanDefinitionDocumentReader();
        documentReader.registerBeanDefinitions(document, beanFactory);
    }

}
