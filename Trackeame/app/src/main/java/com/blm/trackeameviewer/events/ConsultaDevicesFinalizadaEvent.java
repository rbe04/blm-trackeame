package com.blm.trackeameviewer.events;

import java.util.List;

import com.blm.trackeameviewer.model.Device;

public class ConsultaDevicesFinalizadaEvent {
    
    private List<Device> listaDevices;
    
    

    public List<Device> getListaDevices() {
        return listaDevices;
    }

    public void setListaDevices(List<Device> listaDevices) {
        this.listaDevices = listaDevices;
    }
    
    

}
