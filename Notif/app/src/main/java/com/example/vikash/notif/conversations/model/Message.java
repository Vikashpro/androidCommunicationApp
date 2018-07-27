package com.example.vikash.notif.conversations.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("c_id")
    @Expose
    private String c_id ;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("p_email")
    @Expose
    private String pEmail;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("m_id")
    @Expose
    private String mId;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    public Message (String name,String email,String image,String message,String timestamp){

        this.name = name;

        this.image = image;
        this.pEmail = email;
        this.message = message;
        this.timestamp = timestamp;
    }
    public Message(){}

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPEmail() {
        return pEmail;
    }

    public void setPEmail(String pEmail) {
        this.pEmail = pEmail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMId() {
        return mId;
    }

    public void setMId(String mId) {
        this.mId = mId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
