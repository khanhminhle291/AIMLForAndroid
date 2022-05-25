package com.ak.aimlforandroid.UI.Models;

public class Classroom {
    private String id;
    private String ten;
    private String phan;
    private String phong;
    private String chuDe;
    private String moTa;
    private String uid_nguoiTao;

    public Classroom() {
    }

    public Classroom(String id, String ten, String phan, String phong, String chuDe, String uid_nguoiTao) {
        this.id = id;
        this.ten = ten;
        this.phan = phan;
        this.phong = phong;
        this.chuDe = chuDe;
        this.uid_nguoiTao = uid_nguoiTao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getPhan() {
        return phan;
    }

    public void setPhan(String phan) {
        this.phan = phan;
    }

    public String getPhong() {
        return phong;
    }

    public void setPhong(String phong) {
        this.phong = phong;
    }

    public String getChuDe() {
        return chuDe;
    }

    public void setChuDe(String chuDe) {
        this.chuDe = chuDe;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getUid_nguoiTao() {
        return uid_nguoiTao;
    }

    public void setUid_nguoiTao(String uid_nguoiTao) {
        this.uid_nguoiTao = uid_nguoiTao;
    }
}
