package com.yicheng6.beans.factory.support;

import java.util.LinkedList;

/**
 * Created by yicheng6 on 15/12/27.
 */
public class GenericBeanDefinition {

    private volatile Object beanClass;

    private String beanName;

    private LinkedList<ValueHolder> constructorArgumentValues = new LinkedList<ValueHolder>();

    public void setBeanName(String beanName) { this.beanName = beanName; }

    public String getBeanName() { return this.beanName; }

    public void setBeanClass(String beanClassName) {
        this.beanClass = beanClassName;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Object getBeanClass() { return this.beanClass; }

    public LinkedList<ValueHolder> getConstructorArgumentValues() { return this.constructorArgumentValues; }
}
