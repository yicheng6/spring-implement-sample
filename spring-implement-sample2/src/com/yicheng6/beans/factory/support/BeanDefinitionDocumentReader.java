package com.yicheng6.beans.factory.support;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by yicheng6 on 16/1/3.
 */
public class BeanDefinitionDocumentReader {

    public static final String BEAN_ELEMENT = "bean";

    public static final String ID_ATTRIBUTE = "id";

    public static final String NAME_ATTRIBUTE = "name";

    public static final String REF_ATTRIBUTE = "ref";

    public static final String VALUE_ATTRIBUTE = "value";

    public static final String CLASS_ATTRIBUTE = "class";

    public static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";

    private DefaultListableBeanFactory beanFactory;

    public void registerBeanDefinitions(Document document, DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        Element root = document.getDocumentElement();
        doRegisterBeanDefinitions(root);
    }

    private void doRegisterBeanDefinitions(Element root) {
        parseBeanDefinitions(root);
    }

    private void parseBeanDefinitions(Element root) {
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            // Screen out Text, or print "#text" NodeName
            if (node instanceof Element) {
                Element element = (Element) node;
//                System.out.println(element.getNodeName());
                parseDefaultElement(element);
            }
        }
    }

    private void parseDefaultElement(Element element) {
        if (nodeNameEquals(element, BEAN_ELEMENT)) {
            processBeanDefinition(element);
        }
    }

    private void processBeanDefinition(Element element) {
        GenericBeanDefinition beanDefinition = parseBeanDefinitionElement(element);
        registerBeanDefinition(beanDefinition, beanFactory);
    }

    // parse utils
    public boolean nodeNameEquals(Node node, String desiredName) {
        return desiredName.equals(node.getNodeName());
    }

    public GenericBeanDefinition parseBeanDefinitionElement(Element element) {
        String id = element.getAttribute(ID_ATTRIBUTE);

        String className = null;
        if (element.hasAttribute(CLASS_ATTRIBUTE))
            className = element.getAttribute(CLASS_ATTRIBUTE).trim();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanName(id);
        bd.setBeanClass(className);

        parseConstructorArgElements(element, bd);

        return bd;
    }

    public void parseConstructorArgElements(Element beanElement, GenericBeanDefinition bd) {
        NodeList nodeList = beanElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (nodeNameEquals(node, CONSTRUCTOR_ARG_ELEMENT)) {
                parseConstructorArgElement((Element)node, bd);
            }
        }
    }

    public void parseConstructorArgElement(Element element, GenericBeanDefinition bd) {
        String nameAttr = element.getAttribute(NAME_ATTRIBUTE);

        boolean hasRefAttribute = element.hasAttribute(REF_ATTRIBUTE);
        boolean hasValueAttribute = element.hasAttribute(VALUE_ATTRIBUTE);
        if (hasRefAttribute && hasValueAttribute) {
            System.err.print("Not allowed to contain either 'ref' attribute OR 'value' attribute");
        }

        ValueHolder valueHolder = new ValueHolder();
        if (nameAttr != null && !nameAttr.trim().equals("")) {
            valueHolder.setNameAttr(nameAttr);
        }
        if (hasRefAttribute) {
            String refName = element.getAttribute(REF_ATTRIBUTE);
            if (refName != null && !refName.trim().equals("")) {
                valueHolder.setRefName(refName);
            }
        }
        if (hasValueAttribute) {
            String value = element.getAttribute(VALUE_ATTRIBUTE);
            if (value != null && !value.trim().equals("")) {
                valueHolder.setRefName(value);
            }
        }

        bd.getConstructorArgumentValues().add(valueHolder);
    }

    // reader utils
    public static void registerBeanDefinition(GenericBeanDefinition beanDefinition, DefaultListableBeanFactory beanFactory) {
        beanFactory.registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);
    }
}
