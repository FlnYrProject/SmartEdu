package com.project.smartedu.database;

/**
 * Created by Shubham Bhasin on 01-Mar-17.
 */

public class Schedule {

    String day;
    long start_time;
    long end_time;
    String info;

    public Schedule(String day, long start_time, long end_time, String info) {
        this.day = day;
        this.start_time = start_time;
        this.end_time = end_time;
        this.info = info;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
