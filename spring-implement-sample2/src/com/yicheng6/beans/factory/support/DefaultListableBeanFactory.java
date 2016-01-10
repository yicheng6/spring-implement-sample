package com.yicheng6.beans.factory.support;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
        // 判断是否已被实例化
        Object singletonObject = this.singletonObjects.get(name);
        if (singletonObject == null) {
            synchronized (this.singletonObjects) {
                GenericBeanDefinition bd = this.beanDefinitionMap.get(name);
                Object className = bd.getBeanClass();
                List<ValueHolder> argumentValues = bd.getConstructorArgumentValues();
                List<ValueHolder> propertyValues = bd.getPropertyValues();
                if (className instanceof String) {
                    Class<?> clazz = null;
                    try {
                        clazz = beanClassLoader.loadClass((String)className);
//                        System.out.println(clazz.getDeclaredFields()[0].toString());
                        // 判断实例化是否需要参数,对需要参数实例化
                        if (argumentValues.size() > 0) {
                            singletonObject = autowireConstructor(clazz, name, argumentValues);
                        } else {
                            singletonObject = instantiate(clazz, name);
                        }
                        this.singletonObjects.put(name, (singletonObject != null ? singletonObject : null));

                        // 判断是否setter injection
                        if (propertyValues.size() > 0) {
                            applyPropertyValues(singletonObject, propertyValues);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new Exception("",e);
                    }
                }
            }
        }

        return singletonObject;
    }

    public Object autowireConstructor(Class<?> clazz, String name, List<ValueHolder> argumentValues) throws Exception {
        Constructor<?>[] candidates = clazz.getDeclaredConstructors();
        for (int i = 0; i < candidates.length; i++) {
            Constructor<?> candidate = candidates[i];
            Parameter[] parameters = candidate.getParameters();

            if (parameters.length != argumentValues.size()) {
                continue;
            }

            // 匹配参数一致的构造器,并实例化参数封装到Object数组中
            boolean isCandidate = true;
            Object[] paramObject = new Object[parameters.length];
            for (int paramIndex = 0; paramIndex < parameters.length; paramIndex++) {
                Parameter parameter = parameters[paramIndex];

                for (ValueHolder argumentValue : argumentValues) {
                    if(argumentValue.getNameAttr().equals(parameter.getName())) {
                        if (argumentValue.getRefName() != null && !argumentValue.getRefName().equals("")) {
                            try {
                                paramObject[paramIndex] = getBean(argumentValue.getRefName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (argumentValue.getValue() != null && !argumentValue.getValue().equals("")) {
                            // 暂时仅处理String
                            paramObject[paramIndex] = argumentValue.getValue();
                        }
                    }
                }

                if (paramObject[paramIndex] == null) {
                    isCandidate = false;
                    break;
                }
            }

            // 调用构造器,完成bean的实例化
            if (isCandidate) {
                try {
                    return candidate.newInstance(paramObject);
                } catch (InstantiationException e) {
                    throw new Exception(clazz + "InstantiationException", e);
                } catch (IllegalAccessException e) {
                    throw new Exception(clazz + "IllegalAccessException", e);
                } catch (InvocationTargetException e) {
                    throw new Exception(clazz + "InvocationTargetException", e);
                }
            } else {
                continue;
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

    public void applyPropertyValues(Object singletonObject, List<ValueHolder> propertyValues) throws Exception {
        for (ValueHolder propertyValue : propertyValues) {
            try {
                PropertyDescriptor pd = new PropertyDescriptor(propertyValue.getNameAttr(), singletonObject.getClass());
                Method setter = pd.getWriteMethod();
                Object param = null;
                if (propertyValue.getRefName() != null && !propertyValue.getRefName().equals("")) {
                    try {
                        param = getBean(propertyValue.getRefName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (propertyValue.getValue() != null && !propertyValue.getValue().equals("")) {
                    // 暂时仅处理String
                    param = propertyValue.getValue();
                }
                setter.invoke(singletonObject, new Object[] { param });
            } catch (IntrospectionException e) {
                throw new Exception(singletonObject.getClass() + "+IntrospectionException:" + propertyValue.getNameAttr(), e);
            }
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
