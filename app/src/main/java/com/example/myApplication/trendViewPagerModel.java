package com.example.myApplication;

public class trendViewPagerModel {
    private String title;
    private String desc;
    private String ID;
    private String img;


    public trendViewPagerModel(String title, String desc, String img, String ID) {
        this.title = title;
        this.desc = desc;
        this.img = img;
        this.ID = ID;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public trendViewPagerModel(){
        //empty constructor
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

}
