package com.example.findany;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageView MyAccount;
    ImageView InternalView;
    ImageView StudentsDetails;
    ImageView TeachersProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyAccount=findViewById(R.id.MyAccount);
        InternalView=findViewById(R.id.InternalView);
        StudentsDetails=findViewById(R.id.StudentsDetails);
        TeachersProfile=findViewById(R.id.TeachersProfile);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        MyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,UserAccount.class);
                startActivity(intent);
            }
        });
        StudentsDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,StudentProfiles.class);
                startActivity(intent);
            }
        });
        InternalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,InternalView.class);
                startActivity(intent);
            }
        });
    }
}