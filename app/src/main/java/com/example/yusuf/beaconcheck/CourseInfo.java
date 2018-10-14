package com.example.yusuf.beaconcheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CourseInfo extends AppCompatActivity {
    TextView courseNameView;
    TextView courseIdView;
    LinearLayout pastLecturesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);
        Bundle extras = getIntent().getExtras();
        String courseName = "00000";
        if(extras != null){
            courseName = extras.getString("courseName");
        }

        SharedPreferences instructorCourses = getApplicationContext().getSharedPreferences("InstructorCourses", 0);
        String courseInfoString = instructorCourses.getString(courseName, "");
        courseNameView = findViewById(R.id.courseTitle);
        courseIdView = findViewById(R.id.courseIdLabel);
        pastLecturesLayout = findViewById(R.id.pastLecturesLayout);

        if(courseInfoString.length() < 5){
            courseNameView.setText("Course not found error.");
        } else {
            courseNameView.setText(courseName);
            courseIdView.setText(String.format("ID: %s", courseInfoString.substring(0, 5)));
        }
        String[] lectureDates = getLectureDates(courseInfoString);
        if(lectureDates.length == 0){
            TextView noCourses = new TextView(this);
            noCourses.setText("No lectures yet!");
            noCourses.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            pastLecturesLayout.addView(noCourses);
        }
        else{
            for(String date: lectureDates) {
                Button courseButton = new Button(this);
                courseButton.setText(date);
                courseButton.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                pastLecturesLayout.addView(courseButton);
            }
        }



    }

    public String[] getLectureDates(String courseInfo){
        courseInfo = courseInfo.substring(5, courseInfo.length());
        ArrayList<String> dates = new ArrayList<String>();
        for(int i = 0; i < courseInfo.length(); i++){
            if (courseInfo.substring(i, i+2) == "--"){
                dates.add(courseInfo.substring(i+2, i+10));
            }
        }
        String[] ret = new String[dates.size()];
        dates.toArray(ret);
        return ret;
    }

    public void launchInstructorHome(View view){
        Intent mainIntent = new Intent(this, InstructorHome.class);
        startActivity(mainIntent);
    }
}
