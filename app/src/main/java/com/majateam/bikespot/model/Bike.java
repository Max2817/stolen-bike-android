package com.majateam.bikespot.model;

import android.text.format.DateFormat;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Nicolas Martino on 08/06/15.
 */
public class Bike implements ClusterItem {

    private String id;
    private String lat;
    private String lng;
    private long date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getDate() {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(date*1000);
        String format;
        if(Locale.getDefault().toString().split("_")[0].equals(Locale.FRENCH.toString())){
            format = "dd/MM/yyyy";
        }else{
            format = "MM-dd-yyyy";
        }
        return DateFormat.format(format, cal).toString();
    }

    public void setDate(long date) {
        this.date = date;
    }

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public Bike() {

    }

    @Override
    public LatLng getPosition() {
        return new LatLng(Double.valueOf(lat), Double.valueOf(lng));
    }

}
