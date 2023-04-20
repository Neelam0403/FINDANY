package com.example.findany;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class RecyclerClickListner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_click_listner);

        Intent intent=getIntent();
        String name=intent.getStringExtra("name");
        Toast.makeText(this,name,Toast.LENGTH_SHORT).show();

    }
}