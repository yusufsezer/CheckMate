package com.example.yusuf.beaconcheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddFriends extends AppCompatActivity {
    EditText email;
    EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        email = findViewById(R.id.friendEmailEditText);
        name = findViewById(R.id.friendNameEditText);
    }

    public void onBackClicked(View view){
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void onAddFriendClicked(View view){
        SharedPreferences friends = getApplicationContext().getSharedPreferences("Friends", 0);
        SharedPreferences.Editor editor = friends.edit();
        editor.putString(name.getText().toString(), email.getText().toString());
        editor.commit();
        name.setText("");
        email.setText("");
    }
}
