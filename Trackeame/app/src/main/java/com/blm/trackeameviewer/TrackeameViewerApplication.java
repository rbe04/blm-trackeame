package com.blm.trackeameviewer;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import android.app.Application;

public class TrackeameViewerApplication extends Application {
    
    private static Bus eventBus;
    
    /**
     * Obtención del bus de eventos de otto. Se utiliza lazy initialization para 
     * crear la instancia hasta el momento en que esta necesitada.
     * 
     * @return Bus de eventos de Otto.
     */
    public static Bus getEventBus() {
        if (eventBus == null) {
            eventBus = new Bus(ThreadEnforcer.ANY);
        }
        return eventBus;
    }

}
