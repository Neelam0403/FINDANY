package com.example.findany;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class StudentProfiles extends AppCompatActivity implements RecyclerviewListner {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    List<ModelClass> userdetails;
    RecyclerviewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profiles);
        initdata();
        initRecycleview();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profilebarlayout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                Intent intent=new Intent(this,addprofile.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initdata() {

        userdetails =new ArrayList<>();
        userdetails.add(new ModelClass(R.drawable.googlesignin,"Neelam Madhusudhan Reddy","19","CSE"));
        userdetails.add(new ModelClass(R.drawable.googlesignin,"Madhu","4","CSE"));
        userdetails.add(new ModelClass(R.drawable.googlesignin,"Madhu","14","CSE"));
        userdetails.add(new ModelClass(R.drawable.googlesignin,"Madhu","24","CSE"));
        userdetails.add(new ModelClass(R.drawable.googlesignin,"Madhu","34","CSE"));
        userdetails.add(new ModelClass(R.drawable.googlesignin,"Madhu","44","CSE"));
        userdetails.add(new ModelClass(R.drawable.googlesignin,"Madhu",null,"CSE"));

    }

    private void initRecycleview() {

        recyclerView=findViewById(R.id.recyclerview);
        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new RecyclerviewAdapter(userdetails,this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void OnItemListner(int position) {


    }
}