package com.example.yusuf.beaconcheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class HoldLecture extends AppCompatActivity {
    String courseName;
    String courseId;
    ArrayList<String> students;
    LinearLayout studentLayout;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hold_lecture);
        Bundle extras = getIntent().getExtras();
        courseName = "00000";
        if(extras != null){
            courseName = extras.getString("courseName");
        }
        SharedPreferences instructorCourses = getApplicationContext().getSharedPreferences("InstructorCourses", 0);
        String courseInfoString = instructorCourses.getString(courseName, "");
        if(courseInfoString.length() < 5){
            courseInfoString = "00000";
        }
        studentLayout = findViewById(R.id.checkedInStudentsLayout);
        students = new ArrayList<>();
        courseId = courseInfoString.substring(0, 5);

        startBluetoothClient();
    }

    public void launchInstructorHome(View view){
        SharedPreferences instructorCourses = getApplicationContext().getSharedPreferences("InstructorCourses", 0);
        SharedPreferences.Editor editor = instructorCourses.edit();
        String courseInfoString = instructorCourses.getString(courseName, "");
        String newLectureString = "--10142018";
        for(String student: students){
            newLectureString += "-" + student;
        }
        thread.interrupt();
        editor.putString(courseName, courseInfoString+newLectureString);
        editor.commit();
        Intent courseInfoIntent = new Intent(this, CourseInfo.class);
        courseInfoIntent.putExtra("courseName", courseName);
        startActivity(courseInfoIntent);
    }

    public void addStudent(String student){
        boolean contains = false;
        for(String s: students){
            if (s.equals(student)){
                contains = true;
            }
        }
        if(!contains) {
            students.add(student);
            TextView newStudent = new TextView(this);
            newStudent.setText(student);
            newStudent.setTextSize(30);
            studentLayout.addView(newStudent);
        }
    }


    protected void startBluetoothClient(){
        Log.d("BleClient", "ABOUT TO START THREAD");
        final BleClient bleClient = new BleClient(getApplicationContext(), this, courseId);
        thread = new Thread(new Runnable() {
            public void run() {
                bleClient.run();
            }
        });
        thread.start();
    }

}
