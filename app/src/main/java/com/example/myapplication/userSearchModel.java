package com.example.myapplication;

public class userSearchModel {
    String Username;
    String UserID;


    userSearchModel(){
        //empty constructor
    }

    public userSearchModel(String username, String UserID) {
        Username = username;
        this.UserID=UserID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
