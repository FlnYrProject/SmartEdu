package com.project.smartedu.database;

import java.util.ArrayList;

/**
 * Created by Shubham Bhasin on 20-May-17.
 */

public class Teachers {

    String name;
    String user_id;
    String serial_number;
    ArrayList<String> subjects;

    public Teachers(String name, String user_id, String serial_number, ArrayList<String> subjects) {
        this.name = name;
        this.user_id = user_id;
        this.serial_number = serial_number;
        this.subjects = subjects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }
}
