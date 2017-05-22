package com.project.smartedu;

/**
 * Created by Shubham Bhasin on 26-Mar-17.
 */

public class Model{
    String name;
    int value; /* 0 -&gt; checkbox disable, 1 -&gt; checkbox enable */
    private boolean checked = false;

    public Model(String name, int value){
        this.name = name;
        this.value = value;
    }


    public String getName(){
        return this.name;
    }

    public int getValue(){
        return this.value;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
        this.value=1;
    }


}