package com.app.partyplanetvendor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
         getId();
    }
    private void getId() {
        start=findViewById(R.id.start);
        start.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==start)
        {
            startActivity(new Intent(SplashActivity.this,NextActivity.class));
        }
    }


}