package com.project.smartedu.database;

import java.util.HashMap;

/**
 * Created by Shubham Bhasin on 04-Mar-17.
 */

public class Class {

    String classid;
    HashMap<String,String> subjects;        //map from subject name to teacher id
    HashMap<String,HashMap<String,String>> teachers;    //map from  teacher id to the details

    public Class(String classid, HashMap<String, String> subjects, HashMap<String, HashMap<String, String>> teachers) {
        this.classid = classid;
        this.subjects = subjects;
        this.teachers = teachers;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public HashMap<String, HashMap<String, String>> getTeachers() {
        return teachers;
    }

    public void setTeachers(HashMap<String, HashMap<String, String>> teachers) {
        this.teachers = teachers;
    }

    public HashMap<String, String> getSubjects() {
        return subjects;
    }

    public void setSubjects(HashMap<String, String> subjects) {
        this.subjects = subjects;
    }


}
