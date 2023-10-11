package com.maxstudio.lotto.Models;

public class Messages implements Comparable
{
    private String from,you, message, type, to, messageId, time, date, name, groupId,image, status, username;

    private Messages()
    {

    }

    private Messages(String from, String message, String type, String to,
                     String messageId, String status, String time, String date,
                     String name, String groupId,String image,String username, String you)
    {
        this.from = from;
        this.message = message;
        this.type = type;
        this.to = to;
        this.messageId = messageId;
        this.time = time;
        this.date = date;
        this.name = name;
        this.groupId = groupId;
        this.status = status;
        this.image = image;
        this.username = username;
        this.you = you;

    }

    public String getYou() {
        return you;
    }

    public void setYou(String you) {
        this.you = you;
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
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    @Override
    public int compareTo(Object o) {

        Messages messages = (Messages) o;
        if (messages.getMessageId().equals(this.messageId))
        {
            return 0;
        }

        return 1;
    }
}
