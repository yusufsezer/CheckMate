package com.example.yusuf.beaconcheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class JoinCourse extends AppCompatActivity {
    EditText courseName;
    EditText courseId;
    EditText email;
    Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_course);
        courseName = findViewById(R.id.courseNameEditText);
        courseId = findViewById(R.id.courseIdEditText);
        email = findViewById(R.id.emailEditText);
        joinButton = findViewById(R.id.joinButton);
    }

    public void onJoinClicked(View view){
        String name = courseName.getText().toString();
        String id = courseId.getText().toString();
        if( name == "" || id == ""){
            return;
        }
        SharedPreferences courses = getApplicationContext().getSharedPreferences("StudentCourses", 0);
        SharedPreferences.Editor editor = courses.edit();
        editor.putString(name, id);
        editor.commit();
        SharedPreferences emailPref = getApplicationContext().getSharedPreferences("Email", 0);
        editor = emailPref.edit();
        editor.putString("email", email.getText().toString());
        editor.commit();
        // Switch activity
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }
}
