package com.maxstudio.lotto.Models;

public class DailyResultsPicker {

    public String date,event, lotto1,lotto2,lotto3,lotto4,lotto5,
            mach1, mach2,mach3,mach4, mach5,name,time,message,msgCount,image,key;

    public int viewType;

    public DailyResultsPicker()
    {

    }


    public DailyResultsPicker(String date, String event, String lotto1, String lotto2,
                              String lotto3, String lotto4,
                              String lotto5, String mach1, String mach2, String mach3,
                              String mach4,String key,
                              String mach5, String name, String time, String msgCount,
                              String message, String image,int viewType) {

        this.date = date;
        this.event = event;
        this.lotto1 = lotto1;
        this.lotto2 = lotto2;
        this.lotto3 = lotto3;
        this.lotto4 = lotto4;
        this.lotto5 = lotto5;
        this.mach1 = mach1;
        this.mach2 = mach2;
        this.mach3 = mach3;
        this.mach4 = mach4;
        this.mach5 = mach5;
        this.key = key;
        this.viewType = viewType;

        this.name = name;
        this.time = time;
        this.msgCount = msgCount;
        this.message = message;
        this.image = image;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }


    public String getLotto1() {
        return lotto1;
    }

    public void setLotto1(String lotto1) {
        this.lotto1 = lotto1;
    }

    public String getLotto2() {
        return lotto2;
    }

    public void setLotto2(String lotto2) {
        this.lotto2 = lotto2;
    }

    public String getLotto3() {
        return lotto3;
    }

    public void setLotto3(String lotto3) {
        this.lotto3 = lotto3;
    }

    public String getLotto4() {
        return lotto4;
    }

    public void setLotto4(String lotto4) {this.lotto4 = lotto4; }

    public String getLotto5() {
        return lotto5;
    }

    public void setLotto5(String lotto5) {
        this.lotto5 = lotto5;
    }


    public String getMach1() {
        return mach1;
    }

    public void setMach1(String mach1) {
        this.mach1 = mach1;
    }

    public String getMach2() {
        return mach2;
    }

    public void setMach2(String mach2) {
        this.mach2 = mach2;
    }

    public String getMach3() {
        return mach3;
    }

    public void setMach3(String mach3) {
        this.mach3 = mach3;
    }

    public String getMach4() {
        return mach4;
    }

    public void setMach4(String mach4) {
        this.mach4 = mach4;
    }

    public String getMach5() {
        return mach5;
    }

    public void setMach5(String mach5) {
        this.mach1 = mach5;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(String msgCount) {
        this.msgCount = msgCount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
