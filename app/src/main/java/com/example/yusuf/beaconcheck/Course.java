package com.example.yusuf.beaconcheck;

public class Course {
    private String id;
    private String courseName;

    public Course(String courseName, String id){
        this.id = id;
        this.courseName = courseName;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.courseName;
    }

}
