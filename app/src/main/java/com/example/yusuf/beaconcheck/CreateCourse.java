package com.example.yusuf.beaconcheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.lang.Math;

public class CreateCourse extends AppCompatActivity {
    EditText courseName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        courseName = findViewById(R.id.courseNameEditText);
    }

    public void launchInstructorHome(View view){
        Intent mainIntent = new Intent(this, InstructorHome.class);
        startActivity(mainIntent);
    }

    public void launchCourseInformation(View view){
        // OK Button clicked.
        SharedPreferences instructorCourses = getApplicationContext().getSharedPreferences("InstructorCourses", 0);
        SharedPreferences.Editor editor = instructorCourses.edit();
        String courseId = Integer.toString((int)(Math.random()*100000));
        while(courseId.length() < 5){
            courseId = "0" + courseId;
        }
        editor.putString(courseName.getText().toString(), courseId);
        editor.commit();
        Intent courseInfoIntent = new Intent(this, CourseInfo.class);
        courseInfoIntent.putExtra("courseName", courseName.getText().toString());
        startActivity(courseInfoIntent);
    }
}
