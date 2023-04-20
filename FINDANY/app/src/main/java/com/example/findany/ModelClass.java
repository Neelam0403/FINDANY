package com.example.findany;

public class ModelClass {

    private int profileimage;
    private String name;
    private String year;
    private String branch;

    public ModelClass(int profileimage,String name,String year,String branch){
        this.profileimage=profileimage;
        this.name=name;
        this.year=year;
        this.branch=branch;
    }


    public int getProfileimage() {
        return profileimage;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getBranch() {
        return branch;
    }


}
