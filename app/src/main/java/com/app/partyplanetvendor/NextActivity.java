package com.app.partyplanetvendor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class NextActivity extends AppCompatActivity {
    private TextView textView;
    private RelativeLayout relative;
    String type="";
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        GetId();
    }
    private void GetId()
    {     logo=findViewById(R.id.logo);
        relative=findViewById(R.id.relative);
        textView=(TextView)findViewById(R.id.partyPlanet);
        textView.setVisibility(View.GONE);
        Animation animationleft = AnimationUtils.loadAnimation(NextActivity.this,R.anim.slid_show_fewminuts);
        Animation fadeOut = AnimationUtils.loadAnimation(NextActivity.this,R.anim.fade_out);
        Animation logoin = AnimationUtils.loadAnimation(NextActivity.this,R.anim.slid_logo_anim);
        Animation blink = AnimationUtils.loadAnimation(NextActivity.this,R.anim.slid_blink);
        Animation zoom_in = AnimationUtils.loadAnimation(NextActivity.this,R.anim.zoomin);
        Animation zoom_out = AnimationUtils.loadAnimation(NextActivity.this,R.anim.zoomout);
        Animation zoom_inand_zoomOut = AnimationUtils.loadAnimation(NextActivity.this,R.anim.zoom_inandzoom_out);


        Timer t = new Timer(false);
        t.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        textView.setVisibility(View.VISIBLE);
                        textView.startAnimation(animationleft);
                        animationleft.setFillAfter(true);
                    }
                });
            }
        }, 1000);
        relative.startAnimation(fadeOut);
        fadeOut.setFillAfter(true);

        Timer t1 = new Timer(false);
        t1.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        logo.setVisibility(View.VISIBLE);
                        logo.startAnimation(zoom_inand_zoomOut);
                        zoom_inand_zoomOut.setFillAfter(true);
                    }
                });
            }
        }, 6000);
        relative.startAnimation(fadeOut);
        fadeOut.setFillAfter(true);
        Timer t3 = new Timer(false);
        t1.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                 Intent intent=  new Intent(NextActivity.this, MainActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(intent);

                    }
                });
            }
        }, 9000);

    }
}