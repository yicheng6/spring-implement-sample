# spring-implement-sample2
### tips:
* Decouple BeanDefinitionDocumentReader from XmlBeanDefinitionReader to handle Element parse, add parseConstructorArgElement() to put constructor-arg to BeanDefinition in ValueHolder from xml.
* Add autowireConstructor() method in DefaultListableBeanFactory getBean() method by recursion, to offer the beans after instantiating & args autowiring, enabled the xml constructor-arg config without ordering.
* Use Java8 API:  
getParameters()[0].getName(), and compile with -parameters javac command line option, or print it: "arg0"!
