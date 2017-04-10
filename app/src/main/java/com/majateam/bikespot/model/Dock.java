package com.majateam.bikespot.model;

import org.parceler.Parcel;

import io.realm.DockRealmProxy;
import io.realm.RealmObject;

/**
 * Stolen Bike Created by Nicolas Martino on 08/06/15.
 */
@Parcel(implementations = { DockRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { Dock.class })
public class Dock extends RealmObject {
    private String lat;
    private String lng;

    String getLat() {
        return lat;
    }

    void setLat(String lat) {
        this.lat = lat;
    }

    String getLng() {
        return lng;
    }

    void setLng(String lng) {
        this.lng = lng;
    }

    public Dock() {
        // Needed public for Parceler
    }
}
