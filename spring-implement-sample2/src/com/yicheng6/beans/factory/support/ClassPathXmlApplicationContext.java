package com.yicheng6.beans.factory.support;

/**
 * Created by yicheng6 on 15/12/27.
 */
public class ClassPathXmlApplicationContext {

    private DefaultListableBeanFactory beanFactory;

    private String location;

    public ClassPathXmlApplicationContext(String configLocation) throws Exception {
        setConfigLocations(configLocation);
        refresh();
    }

    public void refresh() throws Exception {
        refreshBeanFactory();
        finishBeanFactoryInitialization();
    }

    public void setConfigLocations(String location) {
        if (location != null)
            this.location = location;
    }

    private void refreshBeanFactory() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        loadBeanDefinitions(beanFactory);
        this.beanFactory = beanFactory;
    }

    private void finishBeanFactoryInitialization() throws Exception {
        beanFactory.preInstantiateSingletons();
    }

    private void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws Exception {
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.setLocation(location);
        reader.loadBeanDefinitions();
    }

    public <T> T getBean(String name, Class<T> requiredType) {
        return beanFactory.getSingletonObject(name, requiredType);
    }
}
