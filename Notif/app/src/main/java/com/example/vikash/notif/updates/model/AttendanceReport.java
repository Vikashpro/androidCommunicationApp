package com.example.vikash.notif.updates.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vikash on 7/1/18.
 */

public class AttendanceReport {

    @SerializedName("a_id")
    @Expose
    private String aId;
    @SerializedName("teacher_name")
    @Expose
    private String teacherName;
    @SerializedName("semester")
    @Expose
    private String semester;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("month")
    @Expose
    private String month;
    @SerializedName("file")
    @Expose
    private String file;

    public String getAId() {
        return aId;
    }

    public void setAId(String aId) {
        this.aId = aId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
