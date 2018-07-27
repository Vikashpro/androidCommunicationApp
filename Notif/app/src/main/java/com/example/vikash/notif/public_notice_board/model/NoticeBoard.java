package com.example.vikash.notif.public_notice_board.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoticeBoard {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @SerializedName("public_notices")
    @Expose
    private List<PublicNotice> publicNotices = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<PublicNotice> getPublicNotices() {
        return publicNotices;
    }

    public void setPublicNotices(List<PublicNotice> publicNotices) {
        this.publicNotices = publicNotices;
    }

}

