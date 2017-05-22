package com.project.smartedu;

import android.util.Log;

/**
 * Created by Shubham Bhasin on 22-May-17.
 */

public class MarksModel{
    String name;
    int value; /*marks value */


    public MarksModel(String name, int value){
        this.name = name;
        this.value = value;

    }


    public String getName(){
        return this.name;
    }

    public int getValue(){
        return this.value;
    }

    public void setValue(int value){
        this.value=value;

    }





}