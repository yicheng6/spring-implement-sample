package com.yicheng6.test;

import com.yicheng6.beans.factory.support.ClassPathXmlApplicationContext;

/**
 * Created by yicheng6 on 16/1/4.
 */
public class Test {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("service.xml");
        UserService userService = context.getBean("userService", UserService.class);
        userService.say();
    }

}
