package com.blm.trackeameviewer.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Localizacion {
	
	// Location
	boolean gps_enabled = false;
	boolean network_enabled = false;
	private LocationManager locationManager;
	private LatLng coordenada;
	
	public LatLng location(Activity active) {
  		// Obtenemos una referencia al LocationManager
  		if (locationManager == null) {
  			Log.i("","location manager null");
  			locationManager = (LocationManager) active.getSystemService(Context.LOCATION_SERVICE);
  		}

  		// exceptions will be thrown if provider is not permitted.
  		try {
  			network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
  			if (network_enabled) {
  				Log.i("","NETWORK ENABLED");
  				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100000,locationListenerNetwork);
  			}
  		} catch (Exception ex) {
  			Log.e("", ex.getMessage());
  		}

  		try {
  			gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  			if (gps_enabled) {
  				Log.i("","GPS ENABLED");
  				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1,locationListenerGps);
  			}
  		} catch (Exception ex) {
  			Log.e("", ex.getMessage());
  		}
		return coordenada;
  	}
      
  	// Listener para recibir actualizaciones de la posición
  	// Listener GPS
  	LocationListener locationListenerGps = new LocationListener() {
  		public void onLocationChanged(Location location) {
  			Log.i("","listener GPS");
  			locationManager.removeUpdates(locationListenerNetwork);
  			
  			//mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(
				//	new LatLng(location.getLatitude(), location.getLongitude()), 18));
  			coordenada = new LatLng(location.getLatitude(), location.getLongitude());
  			Log.i("log", "ENVIA COORDENADAS GPS");
  		}

  		public void onProviderDisabled(String provider) {
  		}

  		public void onProviderEnabled(String provider) {
  		}

  		public void onStatusChanged(String provider, int status, Bundle extras) {
  		}
  	};

  	// Listener NETWORK
  	LocationListener locationListenerNetwork = new LocationListener() {
  		public void onLocationChanged(Location location) {
  			Log.i("log","listener NETWORK");
			//mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(
				//	new LatLng(location.getLatitude(), location.getLongitude()), 18));
  			coordenada = new LatLng(location.getLatitude(), location.getLongitude());
  			
			Log.i("log", "ENVIA COORDENADAS RED");
  		}

  		public void onProviderDisabled(String provider) {
  		}

  		public void onProviderEnabled(String provider) {
  		}

  		public void onStatusChanged(String provider, int status, Bundle extras) {
  		}
  	};
	
	
	public static  Location getLastKnownLocation(Context activity) {
		Location location = null;
		LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		location = getProvider(locationManager,LocationManager.GPS_PROVIDER);
		if(location == null){
			location = getProvider(locationManager,LocationManager.NETWORK_PROVIDER);			
		}
		locationManager = null;
		return location;
	}

	private static Location getProvider(LocationManager lm, String prov) {
		Location loc = null;
		try {
			loc = lm.getLastKnownLocation(prov);
			if(loc != null)
				Log.i("LastKnownLocation, " + prov ," latitude: " +  loc.getLatitude() + ", longitude: " + loc.getLongitude());
		} catch (Exception e2) {
			Log.e("Exception", "Error: "+e2.getMessage());
			loc = null;
		}
		return loc;
	}


}
