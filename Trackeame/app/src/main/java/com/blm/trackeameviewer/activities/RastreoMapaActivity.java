package com.blm.trackeameviewer.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.blm.trackeameviewer.R;
import com.blm.trackeameviewer.TrackeameViewerApplication;

public class RastreoMapaActivity extends FragmentActivity {
    
    private static final String TAG = RastreoMapaActivity.class.getSimpleName();

    private static final String ID_EXTRA_TRAILERS_EN_GEOCERCA = "trailersEnGEocerca";
    


    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Log.i(TAG, "onCreate TEMP");
        setContentView(R.layout.rastreo_mapa_activity);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        TrackeameViewerApplication.getEventBus().register(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        TrackeameViewerApplication.getEventBus().unregister(this);
    }
    
   
    

}
