package com.example.yusuf.beaconcheck;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout courseLayout = findViewById(R.id.classListContainer);
        Button joinButton = findViewById(R.id.joinClassButton);
        // joinButton.setOnClickListener(new View.OnClickListener());
        Course[] courseList = getCourseList();
        String[] friendList = getFriendData();
        if(courseList.length == 0){
            TextView noCourses = new TextView(this);
            noCourses.setText("No courses yet!");
            noCourses.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            courseLayout.addView(noCourses);
        }
        else{
            for(Course course: courseList){
                TextView courseText = new TextView(this);
                courseText.setText(course.getName());
                courseText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                courseLayout.addView(courseText);
            }
        }
    }

    public Course[] getCourseList(){
        SharedPreferences courses = getApplicationContext().getSharedPreferences("Courses", 0);
        Map<String, Integer> courseMap = (Map<String, Integer>)courses.getAll();
        Course[] ret = new Course[courseMap.size()];
        Object[] courseKeys = courseMap.keySet().toArray();
        for(int i = 0; i < courseKeys.length; i++) {
            ret[i] = new Course((String)courseKeys[i], courseMap.get(courseKeys[i]));
        }
        return ret;
    }

    public String[] getFriendData(){
        SharedPreferences friends = getApplicationContext().getSharedPreferences("Friends", 0);
        return Arrays.copyOf(friends.getAll().keySet().toArray(), friends.getAll().keySet().toArray().length, String[].class);
    }

}
