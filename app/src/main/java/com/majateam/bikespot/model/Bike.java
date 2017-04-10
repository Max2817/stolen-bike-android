package com.majateam.bikespot.model;

import android.text.format.DateFormat;

import org.parceler.Parcel;

import java.util.Calendar;
import java.util.Locale;

import io.realm.BikeRealmProxy;
import io.realm.RealmObject;

/**
 * Stolen Bike Created by Nicolas Martino on 08/06/15.
 */
@Parcel(implementations = {BikeRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Bike.class})
public class Bike extends RealmObject {

    private String id;
    private String lat;
    private String lng;
    private long date;
    private String title;
    private String description;
    private String brand;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    void setLng(String lng) {
        this.lng = lng;
    }

    public String getConvertedDate() {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(date * 1000);
        String format;
        if (Locale.getDefault().toString().split("_")[0].equals(Locale.FRENCH.toString())) {
            format = "dd/MM/yyyy";
        } else {
            format = "MM-dd-yyyy";
        }
        return DateFormat.format(format, cal).toString();
    }

    long getDate() {
        return date;
    }

    void setDate(long date) {
        this.date = date;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    String getBrand() {
        return brand;
    }

    void setBrand(String brand) {
        this.brand = brand;
    }

    public Bike() {
        // Needed public for Parceler
    }
}
