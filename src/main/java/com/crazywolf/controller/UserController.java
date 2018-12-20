package com.crazywolf.controller;

import com.crazywolf.bean.User;
import com.google.common.base.Preconditions;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/29 9:38
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/get")
    public User getUser() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user = new User(1, "haha", "beijing");

        return user;
    }

    @RequestMapping("/test")
    public User test() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new User(1, "haha", "beijing");
    }

    @RequestMapping("/getUsers")
    public List<User> getUsers() {
        List<User> userList = new ArrayList<User>();
        userList.add(new User(2, "234", "345345"));
        userList.add(new User(234, "2dfgsdfg3432", "beijing"));
        userList.add(new User(234, "6768", "lanzhou"));
        System.out.println("getUsers");
        return userList;
    }

    @RequestMapping("/del")
    public boolean delUser() {
        return true;
    }

    @RequestMapping("/update/{id}")
    public boolean update(@PathVariable int id) {
        Preconditions.checkArgument(id > 0, new Exception("id 必须大于0 "));
        System.out.println("=========================");
        System.out.println(id);
        User user = new User(1, "234", "345");
        System.out.println(user.toString());
        System.out.println("=========================");
        return false;
    }
}