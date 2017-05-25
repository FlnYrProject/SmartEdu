package com.project.smartedu.database;

/**
 * Created by Shubham Bhasin on 25-May-17.
 */

public class Notice {

    String noticeid;
    String content;
    String time;
    String bystring;
    String userid;
    String username;

    public Notice() {
    }

    public Notice(String noticeid, String content, String time, String bystring, String uerid, String username) {
        this.noticeid = noticeid;
        this.content = content;
        this.time = time;
        this.bystring = bystring;
        this.userid = uerid;
        this.username = username;
    }

    public String getNoticeid() {
        return noticeid;
    }

    public void setNoticeid(String noticeid) {
        this.noticeid = noticeid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBystring() {
        return bystring;
    }

    public void setBystring(String bystring) {
        this.bystring = bystring;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String uerid) {
        this.userid = uerid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
