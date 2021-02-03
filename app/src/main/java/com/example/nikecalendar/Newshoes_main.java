package com.example.nikecalendar;

public class Newshoes_main {
    private String date;
    private String dday;
    private String name;
    private String price;
    private String imageuri;
    String pId;

    public Newshoes_main(String date, String dday, String name, String price, String imageuri, String pId) {
        this.date = date;
        this.dday = dday;
        this.name = name;
        this.price = price;
        this.imageuri = imageuri;
        this.pId = pId;
    }

    public Newshoes_main() {}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public String getDday() {
        return dday;
    }

    public void setDday(String dday) {
        this.dday = dday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }
}
