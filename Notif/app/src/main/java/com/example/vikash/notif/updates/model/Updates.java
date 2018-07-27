package com.example.vikash.notif.updates.model;

/**
 * Created by vikash on 7/1/18.
 */

public class Updates {
    private String id;
    private String name;
    private String date;
    private String category;
    private String pdf;

    public Updates(String id, String name, String date, String category, String pdf) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.category = category;
        this.pdf = pdf;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getPdf() {
        return pdf;
    }
}
