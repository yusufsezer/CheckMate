package com.example.yusuf.beaconcheck;

public class Course {
    private int id;
    private String courseName;

    public Course(String courseName, int id){
        this.id = id;
        this.courseName = courseName;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.courseName;
    }

}
