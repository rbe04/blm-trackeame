package com.blm.trackeameviewer.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.blm.trackeameviewer.TrackeameViewerApplication;
import com.blm.trackeameviewer.events.ConsultaDevicesFinalizadaEvent;
import com.blm.trackeameviewer.model.Device;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsultaDevicesService extends Service {
    
    private static final String TAG = ConsultaDevicesService.class.getSimpleName(); 

    private AtomicBoolean isConsultandoDevices;
    
    private List<Device> listaDevices;
    

    private String errorResponse= new String();
    
    @Override
    public void onCreate() {
    	Log.d(TAG, "onCreate");
        isConsultandoDevices = new AtomicBoolean(false);
        super.onCreate();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d(TAG, "onStartsCommand");
        isConsultandoDevices = new AtomicBoolean(true);
        consultarDevices();
        return super.onStartCommand(intent, flags, startId);
    }
    
   
    
    private void consultarDevices() {
        new Thread() {
            @Override
            public void run() {
                consumirDevices();
                super.run();
            }
        }.start();
        
    }
    
    private void consumirDevices() {
        SyncHttpClient client;
        client = new SyncHttpClient();
        client.setTimeout(15000);
        String localpaht = "http://207.210.104.159:8181/Trackeame/GetDevicesPosition";
        localpaht=localpaht.replace(" ", "%20");
        Log.e(TAG, "PATH: "+localpaht);
        client.get(localpaht, new JsonHttpResponseHandler() {
            //private ProgressDialog dialog;

            @Override
            public void onStart() {
                Log.i("**START", "Initiated the request FIND");
                  /*dialog = ProgressDialog.show(getApplicationContext(), "Consultando informaci�n",
                  "Espere un momento...", false,true);*/
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("****__RESPONSE Taxis: ", "" + response);
                try {
                    String result = response.getString("result");
                    if(result.equals("ok")){
                        Log.i(TAG, "ok");
                        JSONArray listaDevicesJSON = response.getJSONArray("data");
                        listaDevices = new ArrayList<Device>();
                        for (int i = 0; i < listaDevicesJSON.length(); i++) {
                            
                            Device device = new Device();
                            JSONObject jsonTemp = listaDevicesJSON.getJSONObject(i);
                            
                            device.setId(jsonTemp.getString("iddevice"));
                            device.setLatitud(jsonTemp.getDouble("latitud"));
                            device.setLongitud(jsonTemp.getDouble("longitud"));
                            
                            
                            Log.i(TAG, "device ID: " + device.getId());
                            Log.i(TAG, "device Lat: " + device.getLatitud());
                            Log.i(TAG, "device Lng: " + device.getLongitud());
                            
                            listaDevices.add(device);
                        }
                    }else{
                        errorResponse = response.getString("message");
                    }
                } catch (JSONException e1) {
                    Log.e("ERROR: ", "" + e1.getMessage());
                    //errorResponse = e1.getMessage();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                String errorMessage = "";
                try {
                    errorMessage = error.getString("result");
                } catch (JSONException e1) {
                    Log.e("ERROR: ", "" + e1.getMessage());
                    //errorResponse = e1.getMessage();
                }
                Log.e("", "Por favor intente de nuevo");
                Log.e("**ERROR: ", "" + throwable.getMessage() + " " + errorMessage);
                errorResponse = "ERROR: " + throwable.getMessage() + " " + errorMessage;
            }

            @Override
            public void onFinish() {
                Log.i("**FINISH", "Completed the request FIND");
                //dialog.dismiss();
                isConsultandoDevices.set(false);
                notificarSerivicioTerminado();
            }
        });
    }
    
    public AtomicBoolean isConsultandoDevices() {
        return isConsultandoDevices;
    }
    
    
    private void notificarSerivicioTerminado() {
        ConsultaDevicesFinalizadaEvent finish = new ConsultaDevicesFinalizadaEvent();
        if(!listaDevices.isEmpty()){
            finish.setListaDevices(listaDevices);
        }else{
           // finish.setErrorResponse(errorResponse);
        }
        
        TrackeameViewerApplication.getEventBus().post(finish);

    }
    
    @Override
    public IBinder onBind(Intent intent) {
        
        return null;
    }
    
    public class ConsultaDevicesBinder extends Binder {
        
        public ConsultaDevicesService getService() {
            return ConsultaDevicesService.this;
        }
        
    }
    

}
