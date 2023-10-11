package com.maxstudio.lotto.Models;

public class GroupPicker {

    public String image, name, username, status, from, groupName, groupImage,key;
    public int viewType;

    public GroupPicker()
    {

    }


    public GroupPicker(String image, String name,String groupName,String groupImage,
                       String username, String status, String from,String key,int viewType) {

        this.image = image;
        this.name = name;
        this.username = username;
        this.status = status;
        this.from = from;
        this.groupName = groupName;
        this.groupImage = groupImage;
        this.key = key;
        this.viewType = viewType;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public GroupPicker(String groupName, String groupImage) {

        this.groupName = groupName;
        this.groupImage = groupImage;

    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
