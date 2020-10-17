package com.example.myapplication;

public class userSearchModel {
    String Username;
    String UserId;

    public userSearchModel(String username) {
        Username = username;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
