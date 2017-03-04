package com.project.smartedu.database;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shubham Bhasin on 04-Mar-17.
 */

public class Allotments {

    String teacherid;
    HashMap<String,HashMap<String,String>> allots; //map from class id to the details

    public Allotments(String teacherid, HashMap<String, HashMap<String, String>> allots) {
        this.teacherid = teacherid;
        this.allots = allots;
    }

    public String getTeacherid() {
        return teacherid;
    }

    public void setTeacherid(String teacherid) {
        this.teacherid = teacherid;
    }

    public HashMap<String, HashMap<String, String>> getAllots() {
        return allots;
    }

    public void setAllots(HashMap<String, HashMap<String, String>> allots) {
        this.allots = allots;
    }
}
