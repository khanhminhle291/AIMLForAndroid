package com.ak.aimlforandroid.UI.Models;

public class AttendanceRoom {
    private String ngay;
    private String lop;
    private String time;

    public AttendanceRoom() {
    }

    public AttendanceRoom(String ngay, String lop,String time) {
        this.ngay = ngay;
        this.lop = lop;
        this.time = time;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
