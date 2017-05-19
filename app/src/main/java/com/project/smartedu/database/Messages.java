package com.project.smartedu.database;

/**
 * Created by Shubham Bhasin on 19-May-17.
 */

public class Messages {

    String name;
    String content;
    String time;
    String name_id;
    String message_id;

    public Messages(String name, String content, String time, String name_id,String message_id) {
        this.name = name;
        this.content = content;
        this.time = time;
        this.name_id = name_id;
        this.message_id=message_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getName_id() {
        return name_id;
    }

    public void setName_id(String name_id) {
        this.name_id = name_id;
    }
}
