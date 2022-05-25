package com.ak.aimlforandroid.UI.Models;

public class User {
    private long id;
    private String ten;
    private String ngaySinh;
    private String lop;
    private String gioiTinh;
    private String email;
    private String sdt;
    private String loaiTaiKhoan;
    private String urlAnhDaiDien;

    public User() {
    }

    public User(long id, String ten, String lop, String email, String loaiTaiKhoan) {
        this.id = id;
        this.ten = ten;
        this.lop = lop;
        this.email = email;
        this.ngaySinh = null;
        this.gioiTinh = null;
        this.sdt= null;
        this.loaiTaiKhoan = loaiTaiKhoan;
        this.urlAnhDaiDien = null;
    }

    public String getUrlAnhDaiDien() {
        return urlAnhDaiDien;
    }

    public void setUrlAnhDaiDien(String urlAnhDaiDien) {
        this.urlAnhDaiDien = urlAnhDaiDien;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getLoaiTaiKhoan() {
        return loaiTaiKhoan;
    }

    public void setLoaiTaiKhoan(String loaiTaiKhoan) {
        this.loaiTaiKhoan = loaiTaiKhoan;
    }
}
