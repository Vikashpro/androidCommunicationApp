package com.example.vikash.notif.updates.model;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

    public class RetrieveUpdates {

        @SerializedName("error")
        @Expose
        private String error;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("datesheets")
        @Expose
        private List<Datesheet> datesheets = null;
        @SerializedName("time_table")
        @Expose
        private List<TimeTable> timeTable = null;
        @SerializedName("attendance_report")
        @Expose
        private List<AttendanceReport> attendanceReport = null;

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<Datesheet> getDatesheets() {
            return datesheets;
        }

        public void setDatesheets(List<Datesheet> datesheets) {
            this.datesheets = datesheets;
        }

        public List<TimeTable> getTimeTable() {
            return timeTable;
        }

        public void setTimeTable(List<TimeTable> timeTable) {
            this.timeTable = timeTable;
        }

        public List<AttendanceReport> getAttendanceReport() {
            return attendanceReport;
        }

        public void setAttendanceReport(List<AttendanceReport> attendanceReport) {
            this.attendanceReport = attendanceReport;
        }

}


