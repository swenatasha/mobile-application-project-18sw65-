package com.example.media_security;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Handler handler  =new Handler ();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent intent=new Intent(start.this , MainActivity.class);
                startActivity(intent);
            }
        } , 3000);
    }
}
