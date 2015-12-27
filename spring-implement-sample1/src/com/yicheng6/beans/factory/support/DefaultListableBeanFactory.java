package com.yicheng6.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yicheng6 on 15/12/27.
 */
public class DefaultListableBeanFactory {

    private final Map<String, GenericBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, GenericBeanDefinition>(64);

    private final List<String> beanDefinitionNames = new ArrayList<String>();

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(64);

    private ClassLoader beanClassLoader = this.getClass().getClassLoader();

    public void registerBeanDefinition(String beanName, GenericBeanDefinition beanDefinition) {

        synchronized (this.beanDefinitionMap) {
            Object oldBeanDefinition = this.beanDefinitionMap.get(beanName);
            if (oldBeanDefinition == null) {
                this.beanDefinitionNames.add(beanName);
            }
            this.beanDefinitionMap.put(beanName,beanDefinition);
        }
    }

    public void preInstantiateSingletons() throws Exception {
        List<String> beanNames;
        synchronized (this.beanDefinitionMap) {
            beanNames = new ArrayList<String>(this.beanDefinitionNames);
        }
        for (String beanName : beanNames) {
            getBean(beanName);
        }
    }

    public Object getBean(String name) throws Exception {
        Object singletonObject = this.singletonObjects.get(name);
        if (singletonObject == null) {
            synchronized (this.singletonObjects) {
                GenericBeanDefinition bd = this.beanDefinitionMap.get(name);
                Object className = bd.getBeanClass();
                if (className instanceof String) {
                    Class<?> clazz = null;
                    try {
                        clazz = beanClassLoader.loadClass((String)className);
//                        System.out.println(clazz.getDeclaredFields()[0].toString());
                        singletonObject = instantiate(clazz, name);
                        this.singletonObjects.put(name, (singletonObject != null ? singletonObject : null));
                    } catch (ClassNotFoundException e) {
                        throw new Exception("",e);
                    }
                }
            }
        }

        return null;
    }

    public Object instantiate(Class<?> clazz, String name) throws Exception {
        Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor((Class[]) null);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new Exception(clazz + "No default constructor found", e);
        }
    }

    public <T> T getSingletonObject(String name, Class<T> requiredType) {
        Object singletonObject = this.singletonObjects.get(name);
        if (requiredType != null && singletonObject != null && requiredType.isAssignableFrom(singletonObject.getClass())) {
            return (T) singletonObject;
        }
        return null;
    }
}
