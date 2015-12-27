package com.yicheng6.test;

import com.yicheng6.beans.factory.support.ClassPathXmlApplicationContext;

/**
 * Created by yicheng6 on 15/12/20.
 */
public class Test {

    private String test;

    public Test () {
        this.test = "spring-implement-sample1";
    }

    public String getTest() {
        return this.test;
    }

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("service.xml");
        Test test = context.getBean("user", Test.class);
        if (test != null) {
            System.out.println("This is " + test.getTest());
        }
    }
}
