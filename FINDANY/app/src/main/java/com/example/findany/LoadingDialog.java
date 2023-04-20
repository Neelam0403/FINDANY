package com.example.findany;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class LoadingDialog  {

    Activity activity;
    AlertDialog alertDialog;
    LoadingDialog(Activity myactivity){
        this.activity=myactivity;
    }

    void startingloding(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater=activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.activity_loading_dialog,null));
        builder.setCancelable(false);

        alertDialog=builder.create();
        alertDialog.show();
    }
    void CancleLoading(){
        alertDialog.dismiss();
    }

}