package com.blm.trackeameviewer.fragments;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.blm.trackeameviewer.R;
import com.blm.trackeameviewer.TrackeameViewerApplication;
import com.blm.trackeameviewer.events.ConsultaDevicesFinalizadaEvent;
import com.blm.trackeameviewer.model.Device;
import com.blm.trackeameviewer.services.ConsultaDevicesService;
import com.blm.trackeameviewer.services.ConsultaDevicesService.ConsultaDevicesBinder;
import com.blm.trackeameviewer.utils.Localizacion;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

public class RastreoMapaFragment extends SupportMapFragment {
	
	private static final String TAG = RastreoMapaFragment.class.getSimpleName();
    
    private View mapaView;
    public static GoogleMap gmap2;
	private boolean procesoEnProgresoFindTrailer;
	private SupportMapFragment supportMap2;
    
    private List<Device> listaDEvices;
    private ConexionServicioConsultaDevices conexionServiceConsultaDevices;
    
    private HashMap<String, Device> hashDevices = new HashMap<String, Device>();
    
    
    ConsultaDevicesService consultaDevicesService;
    boolean mBound = false;
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container,savedInstanceState);
		
		mapaView = inflater.inflate(R.layout.rastreo_mapa_fragment, container, false);
		initComponents();
		iniciarServicioConsultaDevices();
		return mapaView;
	}
    
    @Override
    public void onResume() {
    	super.onResume();
    	TrackeameViewerApplication.getEventBus().register(this);

		Log.d(TAG, "onResume vamos a vincularnos con el servicio");
    	vincularServicioDevices();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
        
        TrackeameViewerApplication.getEventBus().unregister(this);

    	desvincularServicioDevices();
    	
     		if (getActivity().isFinishing()) {
     			Log.d(TAG, "Deteniendo Servicio");
     		    detenerServicioTrailers();
     		}
    }
    
    private void initComponents(){
		Log.d(TAG, "initComponents()");
    	supportMap2 = new SupportMapFragment() {
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                gmap2 = supportMap2.getMap();
                if (gmap2 != null) {
            		gmap2.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            		gmap2.setMyLocationEnabled(true);
            		//gmap.setOnInfoWindowClickListener(CentralMapFragment.this);
            		centrarMapa();
                }
            }
        };
        getChildFragmentManager().beginTransaction().add(R.id.map, supportMap2).commit();
    }

    
    /**
	 * Metodo que utiliza la clase LastKnownLocation para obtener la ubicación del usuario
	 * y centrar el mapa en su ubicación.
	 */
	public void centrarMapa() {
		Localizacion loc = new Localizacion();
		LatLng coordinates = loc.location(getActivity());
		if(coordinates != null){
			Log.d("::: CENTRAL :::", "Coordenadas Nuevas");
			gmap2.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10));
			Log.d(TAG, "latitud1: "+coordinates.latitude);
			Log.d(TAG, "longitud1: "+coordinates.longitude);
		}else{
			Log.d("::: CENTRAL :::", "Sin coordenadas");
			Location concurrentlocation = Localizacion.getLastKnownLocation(getActivity());
			if(concurrentlocation!=null){
				gmap2.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(concurrentlocation.getLatitude(),concurrentlocation.getLongitude()), 10));
				Log.d(TAG, "Coordenadas Viejas");
				Log.d(TAG, "latitud1: "+concurrentlocation.getLatitude());
				Log.d(TAG, "longitud1: "+concurrentlocation.getLongitude());
			}
			else{
				gmap2.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0,0), 2));
			}
		}
	}
    
    private void pintarDevices(List<Device> listaActualizadaDevices) {
        Log.d(TAG, "*** pintarDevices ***");
          for (Device device : listaActualizadaDevices) {
              boolean band = false;
              MarkerOptions mkoptionsDevice = null;
              Marker marker = null;
            
              if(hashDevices.containsKey(device.getId())){
                  Log.d(TAG, "-containsKey");
                  marker = hashDevices.get(device.getId()).getMarker();
                  
              }else{
                  Log.d(TAG, "-ELSE containsKey");
                  mkoptionsDevice = new MarkerOptions();
                  mkoptionsDevice.title(device.getId());
                  mkoptionsDevice.position(new LatLng(device.getLatitud(),device.getLongitud()));
                  mkoptionsDevice.icon(BitmapDescriptorFactory.fromResource(R.drawable.android));
                  mkoptionsDevice.snippet(String.valueOf(device.getId()));
              }
              
              if(marker == null){
                  Log.d(TAG, "-*PINTANDO MARKER PRIMERA VEZ");
                  marker = gmap2.addMarker(mkoptionsDevice);

                  Log.d(TAG, "Marker: " + marker.getId() + " lat: " + marker.getPosition());
              }else{
                  Log.d(TAG, "------UPDATE POSICION MARKER");
                  marker.setPosition(new LatLng(device.getLatitud(),device.getLongitud()));
                  
              }
              
              device.setMarker(marker);
              
          }//fin for markers
          
                   
          for (Device deviceAct : listaActualizadaDevices) {
              Log.d(TAG, "::: Vamos a ACTUALIZAR las listas :::");
              //Actualiza el diccionario existente
              if(hashDevices.containsKey(deviceAct.getId())){
            	  Device oldDevice = hashDevices.get(deviceAct.getId());
                  oldDevice.setLatitud(deviceAct.getLatitud());
                  oldDevice.setLongitud(deviceAct.getLongitud());
              }else{
                  //Si no, lo mete al diccionario
                  Log.d(TAG, "Agregamos el camion: "+deviceAct.getId());
                  hashDevices.put(deviceAct.getId(), deviceAct); //hash inicial para los trucks
              }
          }// fin for ACTUALIZAR
    

    }
    

    
    private void iniciarServicioConsultaDevices() { //Double lat, Double lng
    	Log.d(TAG, "======= iniciarServicioFind =============");
	    this.procesoEnProgresoFindTrailer = true;
	    Intent intent = new Intent(getActivity(), ConsultaDevicesService.class);
        getActivity().startService(intent);
	}
    
    @Subscribe
    public void onDevicesConsultados(ConsultaDevicesFinalizadaEvent event) {
        listaDEvices = event.getListaDevices();
        pintarDevices(event.getListaDevices());
    }

    private void vincularServicioDevices() {
        Log.d(TAG, "** vincularServicio Find ");
        Intent intent = new Intent(getActivity() , ConsultaDevicesService.class);
        this.conexionServiceConsultaDevices = new ConexionServicioConsultaDevices();      
        getActivity().bindService(intent, conexionServiceConsultaDevices
                , Context.BIND_AUTO_CREATE);
    }
    
    private void desvincularServicioDevices() {
    	 try {
	            getActivity().unbindService(conexionServiceConsultaDevices);
	        } catch (IllegalArgumentException e) {
	            Log.w(TAG, "El servicio no se encontraba vinculado", e);
	        }
    }
    
    private void detenerServicioTrailers() {
        procesoEnProgresoFindTrailer = false;
    	getActivity().stopService(new Intent(getActivity(), ConsultaDevicesService.class));
    }
    
    private void reiniciarVariablesProcesoDevices() {
    	procesoEnProgresoFindTrailer = false;
    }
   
    @Subscribe
	public void onFinishServiceTrailer(final ConsultaDevicesFinalizadaEvent finish) {
		getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
            	 //Toast.makeText(getActivity(), "Servicio Terminado FindAllWareHouse", Toast.LENGTH_LONG).show();
            	 procesoEnProgresoFindTrailer = false;
            	 Log.i("=============","finishTAXIS");
            	 if(!finish.getListaDevices().isEmpty()){
            		 Log.i(TAG, "todo chido");
            		 for (Device device : finish.getListaDevices()) {
            			 Log.i(TAG, "*id: "+device.getId());
            			 //Log.i(TAG, "*nombre: "+taxista.getNombre());
            			 //Log.i(TAG, "*unidad: "+taxista.getNoTaxi());
            		 }
            		 listaDEvices = finish.getListaDevices();
            		 pintarDevices(finish.getListaDevices());
            		 new Thread(){
            		     public void run() {
            		    	 try {
            		    		 Log.i(TAG, "fecha1: "+new Date());
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								Log.e(TAG, "Error al dormir hilo servicio"+e.getMessage());
							}
            		    	 Log.i(TAG, "fecha2: "+new Date());
            		    	 iniciarServicioConsultaDevices();
            		    	 /*Log.i(TAG, "latitudServ: "+latitud);
            				 Log.i(TAG, "longitudServ: "+longitud);
            		    	 if(latitud != 0 && longitud != 0)
            		    		 iniciarServicioGarbageTruck(latitud, longitud);
            		    	 else
            		    		 Log.i(TAG, "No hay coordenadas");
            		    		 */
            		     }
            		 }.start();
            		 
	           	 }else{
	           		Log.d(TAG, "Error");
//	           		Toast.makeText(getActivity(), finish.getErrorResponse(), Toast.LENGTH_LONG).show();
	           	 }	        		 
            } 
        });
	}
    
    private class ConexionServicioConsultaDevices implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.v(TAG, "Esta en onServiceConnected intenta iniciar servicio");
            
            ConsultaDevicesBinder servicioBinderDevices = (ConsultaDevicesBinder) binder;
            consultaDevicesService = servicioBinderDevices.getService();
            
            Log.d(TAG, "Se encuentra vinculado al servicio." + consultaDevicesService);
            Log.d(TAG, "Estamos esperando la respuesta del servicio? : " + procesoEnProgresoFindTrailer);
            
            if (!consultaDevicesService.isConsultandoDevices().get() && procesoEnProgresoFindTrailer) {
                Log.d(TAG, "El proceso termino y estamos en conteo ");
                reiniciarVariablesProcesoDevices();
            }
        }

        /**
         * Remueve la {@link OperacionFragment} como Listener del Servicio,
         * en caso de que se rompa el vinculo con el servicio debido a la
         * detención del mismo.
         * 
         * @param className
         *            Nombre concreto del Servicio.
         */
        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "Se desvinculo el servicio por que fue detenido. ");
        }
    }

}
