package com.blm.trackeameviewer.model;

import com.google.android.gms.maps.model.Marker;

public class Device {
    
    private String id;
    
    private double latitud;
    
    private double longitud;
    
    private Marker marker;
    
    public Device(String id, double latitud, double longitud) {
        this.id = id;
        this.latitud = latitud;
        this.longitud = longitud;
    }
    
    public Device () {
        
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker2) {
		this.marker = marker2;
		
	}


}
