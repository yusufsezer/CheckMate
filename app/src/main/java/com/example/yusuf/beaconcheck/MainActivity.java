package com.example.yusuf.beaconcheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
        LinearLayout friendsLayout = findViewById(R.id.friendsContainer);
        TextView friendsTitle = findViewById(R.id.yourFriendsLabel);
        TextView emailView = findViewById(R.id.emailDisplay);
        emailView.setText(getEmail());
        Button joinButton = findViewById(R.id.joinClassButton);
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
            for(Course course: courseList) {
                TextView courseText = new TextView(this);
                courseText.setText(course.getName());
                courseText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                courseLayout.addView(courseText);
            }
        }

        if(friendList.length == 0){
            friendsTitle.setVisibility(View.GONE);
        }
        else{
            for(String friend: friendList) {
                TextView friendView = new TextView(this);
                friendView.setText(friend);
                friendView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                friendsLayout.addView(friendView);
            }
        }
        // startBluetoothServer();
    }

    public Course[] getCourseList(){
        SharedPreferences courses = getApplicationContext().getSharedPreferences("StudentCourses", 0);
        Map<String, String> courseMap = (Map<String, String>)courses.getAll();
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

    public String getEmail(){
        SharedPreferences email = getApplicationContext().getSharedPreferences("Email", 0);
        return email.getString("email", "Anonymous (Add your email when joining a class)");
    }

    public void launchJoinCourse(View view){
        Intent newCourseIntent = new Intent(this, JoinCourse.class);
        startActivity(newCourseIntent);

    }

    public void launchAddFriends(View view){
        Intent addFriendsIntent = new Intent(this, AddFriends.class);
        startActivity(addFriendsIntent);
    }

    public void launchInstructorHome(View view){
        Intent instructorHomeIntent = new Intent(this, InstructorHome.class);
        startActivity(instructorHomeIntent);
    }


    protected void startBluetoothServer(){
        Log.d("BLESERVER", "ABOUT TO START THREAD");
        final BLEGattServer bleServer = new BLEGattServer(getApplicationContext());
        Thread thread = new Thread(new Runnable() {
            public void run() {
                bleServer.run();
            }
        });
        thread.start();
    }

}
