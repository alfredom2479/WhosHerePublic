package com.example.whosherejava;

import android.net.Uri;

public class Student {

    private String name, sid,cid, imageName;
    private Status status;

    enum Status{
        PRESENT, ABSENT, OTHER;
    }

    public Student(){
        sid="";
        cid="";
        name="";
        status=Status.OTHER;
        imageName="";
    }

    public Student(String sid,String cid,String name, Status status, String imageName ){
        this.sid=sid;
        this.cid=cid;
        this.name=name;
        this.status= status;
        this.imageName=imageName;
//        this.face=face;

    }

    public String getSid() { return sid; }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() { return name; }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getImageName() { return imageName; }

}
