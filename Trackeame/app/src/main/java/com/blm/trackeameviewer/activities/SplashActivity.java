package com.blm.trackeameviewer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.blm.trackeameviewer.R;

public class SplashActivity extends Activity {
    
    private Intent miIntent = null;
    private final int SPLASH_TIME = 1500;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                
                miIntent = new Intent(SplashActivity.this,
                        RastreoMapaActivity.class);
                
                SplashActivity.this.startActivity(miIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_TIME);    
    }

}
