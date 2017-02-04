package com.project.smartedu.database;

/**
 * Created by Shubham Bhasin on 04-Feb-17.
 */

public class UserDetailsTable {

    public String name;

    public UserDetailsTable(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
