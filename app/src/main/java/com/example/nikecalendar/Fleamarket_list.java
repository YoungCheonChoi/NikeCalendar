package com.example.nikecalendar;

public class Fleamarket_list {

    String pContent, pId, pTime, PImage, uEmail, uid, pTitle, uDp, uNickName;
    //uDp는 유저 이미지

    public Fleamarket_list(){

    }

    public Fleamarket_list(String pContent, String pId, String pTime, String PImage, String uEmail, String uid, String pTitle, String uDp, String uNickName) {
        this.pContent = pContent;
        this.pId = pId;
        this.pTime = pTime;
        this.PImage = PImage;
        this.uEmail = uEmail;
        this.uid = uid;
        this.pTitle = pTitle;
        this.uDp = uDp;
        this.uNickName = uNickName;
    }

    public String getpContent() {
        return pContent;
    }

    public void setpContent(String pContent) {
        this.pContent = pContent;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getPImage() {
        return PImage;
    }

    public void setPImage(String PImage) {
        this.PImage = PImage;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuNickName() {
        return uNickName;
    }

    public void setuNickName(String uNickName) {
        this.uNickName = uNickName;
    }


}
