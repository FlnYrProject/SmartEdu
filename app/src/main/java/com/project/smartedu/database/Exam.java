package com.project.smartedu.database;

/**
 * Created by Shubham Bhasin on 21-May-17.
 */

public class Exam {

    String id;
    String name;
    String date;
    String max_marks;
    String subject;


    public Exam(){

    }


    public Exam(String id, String name, String date, String max_marks, String subject) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.max_marks = max_marks;
        this.subject = subject;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMax_marks() {
        return max_marks;
    }

    public void setMax_marks(String max_marks) {
        this.max_marks = max_marks;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
