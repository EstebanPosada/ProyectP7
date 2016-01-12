package com.estebanposada.proyectp7;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by usuario on 12/01/2016.
 */
public class splash extends AppCompatActivity {

    private static final long Sp_delay = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent mainIntent = new Intent().setClass(splash.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, Sp_delay);
    }
}
