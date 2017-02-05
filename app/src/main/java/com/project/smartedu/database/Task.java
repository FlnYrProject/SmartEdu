package com.project.smartedu.database;

/**
 * Created by Shubham Bhasin on 05-Feb-17.
 */

public class Task {

    String title;
    String description;
    long date;

    public Task(String title, String description, long date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
