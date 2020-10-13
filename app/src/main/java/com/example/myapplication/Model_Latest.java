package com.example.myapplication;

import android.widget.ImageView;

public class Model_Latest {
    int img;
    String title;
    String desc;
    String tag;
    Long time;
    Integer viewCount;

    Model_Latest()
    {

    }

    public Model_Latest(int img, String title, String desc, String tag, Long time, Integer viewCount) {
        this.img = img;
        this.title = title;
        this.desc = desc;
        this.tag = tag;
        this.time = time;
        this.viewCount = viewCount;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
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
