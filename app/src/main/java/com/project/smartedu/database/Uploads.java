package com.project.smartedu.database;

/**
 * Created by kamya batra on 27/03/2017.
 */

public class Uploads {


    String upload_type;
    String subject;
    String topic;
    String imageUrl;
    String teacher;
    long date;

    public Uploads(String upload_type, String subject, String topic, String imageUrl, String teacher, long date) {

        this.upload_type = upload_type;
        this.subject = subject;
        this.date = date;
        this.imageUrl= imageUrl;
        this.teacher= teacher;
        this.topic = topic;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String name) {
        this.subject = name;
    }

    public String getUploadType() {
        return upload_type;
    }

    public void setUploadType(String upload_type) {
        this.upload_type = upload_type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUrl() {
        return imageUrl;
    }

    public void setUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTeacher() {
        return imageUrl;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }


}
