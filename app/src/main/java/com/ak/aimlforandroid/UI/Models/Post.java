package com.ak.aimlforandroid.UI.Models;

import com.ak.aimlforandroid.Untils.Untils;

import java.util.Date;

public class Post {
    private String topPic;
    private String ngay;
    private String noiDung;
    private String postID;
    public Post() {
    }

    public Post(String topPic, String noiDung) {
        this.topPic = topPic;
        this.noiDung = noiDung;
        this.ngay = (new Date()).toString();
        this.postID = Untils.getSaltString(8);
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getTopPic() {
        return topPic;
    }

    public void setTopPic(String topPic) {
        this.topPic = topPic;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
}
