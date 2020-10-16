package com.example.myapplication;

public class Model_Latest {
    Integer img;
    String title;
    String desc;
    String tag;
    Long time;
    Integer viewCount;
    String ID;


    Model_Latest()
    {

    }

    public Model_Latest(Integer img, String title, String desc, String tag, Long time, Integer viewCount, String ID) {
        this.img = img;
        this.title = title;
        this.desc = desc;
        this.tag = tag;
        this.time = time;
        this.viewCount = viewCount;
        this.ID=ID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
    public Integer getImg() {
        return img;
    }

    public void setImg(Integer img) {
        this.img = img;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
}
