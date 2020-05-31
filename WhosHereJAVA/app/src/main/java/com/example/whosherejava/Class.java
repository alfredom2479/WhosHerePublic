package com.example.whosherejava;

public class Class {

    String cid;
    String name;
    int color;
    int percentage;


    //I know I should not name Classes "Class"... Oops

    public Class(){

    }

    public Class(String cid, String name, int color, int percentage) {
        this.cid=cid;
        this.name = name;
        this.color = color;
        this.percentage = percentage;
    }

    public String getCid() {
        return cid;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
