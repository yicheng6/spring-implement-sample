package com.yicheng6.test;

/**
 * Created by yicheng6 on 16/1/4.
 */
public class UserService {

    private User user;

    private String words;

    public UserService(User user, String words) {
        this.user = user;
        this.words = words;
    }

    private String getUserName() {
        return user.getName();
    }

    private String getWords() {
        return words;
    }

    public void say() {
        System.out.println(getUserName() + " says: " + getWords());
    }

}
