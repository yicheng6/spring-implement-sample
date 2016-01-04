# spring-implement-sample1
### tips:
* ClassPathXmlApplicationContext offers the context to obtain the resource config & to get the bean.
* XmlBeanDefinitionReader register the "xml" to the DefaultListableBeanFactory in GenericBeanDefinition by dom4j.
* DefaultListableBeanFactory offers the bean after instantiating to the context.
