package com.majateam.spotbike.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Nicolas Martino on 08/06/15.
 */
public class Dock implements ClusterItem {
    private String lat;
    private String lng;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public Dock() {}

    @Override
    public LatLng getPosition() {
        return new LatLng(Double.valueOf(lat), Double.valueOf(lng));
    }
}
