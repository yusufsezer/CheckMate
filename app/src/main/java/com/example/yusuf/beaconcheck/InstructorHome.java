package com.example.yusuf.beaconcheck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

public class InstructorHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_home);
        LinearLayout courseLayout = findViewById(R.id.instructorCoursesLayout);
        Course[] instructorCourses = getCourses();

        if(instructorCourses.length == 0){
            TextView noCourses = new TextView(this);
            noCourses.setText("No courses yet!");
            noCourses.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            courseLayout.addView(noCourses);
        }
        else{
            for(Course course: instructorCourses) {
                final String courseName = course.getName();
                final Context currentContext = getApplicationContext();
                Button courseButton = new Button(this);
                courseButton.setText(course.getName());
                courseButton.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                courseButton.setOnClickListener(new View.OnClickListener() {
                                                    public void onClick(View v) {
                                                        Intent courseInfoIntent = new Intent(currentContext, CourseInfo.class);
                                                        courseInfoIntent.putExtra("courseName", courseName);
                                                        startActivity(courseInfoIntent);
                                                    }
                                                });

                courseLayout.addView(courseButton);
            }
        }
    }


    public Course[] getCourses(){
        SharedPreferences instructorCourses = getApplicationContext().getSharedPreferences("InstructorCourses", 0);
        Map<String, String> courseMap = (Map<String, String>)instructorCourses.getAll();
        Course[] ret = new Course[courseMap.size()];
        Object[] courseKeys = courseMap.keySet().toArray();
        for(int i = 0; i < courseKeys.length; i++) {
            ret[i] = new Course((String)courseKeys[i], courseMap.get(courseKeys[i]));
        }
        return ret;
    }

    public void launchStudentHome(View view){
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void launchCreateCourse(View view){
        Intent mainIntent = new Intent(this, CreateCourse.class);
        startActivity(mainIntent);
    }
}
