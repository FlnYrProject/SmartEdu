package com.project.smartedu.database;

/**
 * Created by Shubham Bhasin on 15-Mar-17.
 */

public class Students {

    String userid;
    String class_id;
    String roll_number;


    public Students(String userid, String class_id, String roll_number) {
        this.userid = userid;
        this.class_id = class_id;
        this.roll_number = roll_number;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getRoll_number() {
        return roll_number;
    }

    public void setRoll_number(String roll_number) {
        this.roll_number = roll_number;
    }
}
