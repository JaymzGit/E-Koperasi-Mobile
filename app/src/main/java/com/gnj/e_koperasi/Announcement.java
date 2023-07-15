package com.gnj.e_koperasi;

public class Announcement {
    private String message;
    private String time;
    private String date;
    private String title;

    public Announcement() {
    }

    public Announcement(String message, String time, String date) {
        this.message = message;
        this.time = time;
        this.date = date;
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
