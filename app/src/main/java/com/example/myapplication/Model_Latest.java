package com.example.myapplication;

public class Model_Latest {
    Integer img;
    String title;
    String desc;
    String tag;
    Integer time;
    Integer viewCount;

    Model_Latest()
    {

    }

    public Model_Latest(Integer img, String title, String desc, String tag, Integer time, Integer viewCount) {
        this.img = img;
        this.title = title;
        this.desc = desc;
        this.tag = tag;
        this.time = time;
        this.viewCount = viewCount;
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

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
}
