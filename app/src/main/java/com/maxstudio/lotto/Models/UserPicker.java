package com.maxstudio.lotto.Models;

public class UserPicker {

    public String image,username,device_token,password,email,userId,groups,key;

    public UserPicker()
    {

    }

    public UserPicker(String image, String username, String device_token,String password,
                      String email,String userId,String groups,String key) {

        this.image = image;
        this.username = username;
        this.device_token = device_token;
        this.password = password;
        this.email = email;
        this.userId = userId;
        this.groups = groups;
        this.key = key;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;}
}
