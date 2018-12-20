package com.crazywolf.bean;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/29 9:42
 */
@Setter
@Getter
@AllArgsConstructor
@ToString
public class User {
    public int id;
    public String name;
    public String add;
}