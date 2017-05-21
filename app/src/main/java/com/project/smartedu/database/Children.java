package com.project.smartedu.database;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shubham Bhasin on 21-May-17.
 */

public class Children {

    String name;
    ArrayList<String> insitutions;
    HashMap<String,String> classid;             //insitution-->class


    public Children() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getInsitutions() {
        return insitutions;
    }

    public void setInsitutions(ArrayList<String> insitutions) {
        this.insitutions = insitutions;
    }

    public HashMap<String, String> getClassid() {
        return classid;
    }

    public void setClassid(HashMap<String, String> classid) {
        this.classid = classid;
    }

    public Children(String name, ArrayList<String> insitutions, HashMap<String, String> classid) {

        this.name = name;
        this.insitutions = insitutions;
        this.classid = classid;
    }
}
