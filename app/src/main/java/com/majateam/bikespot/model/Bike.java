package com.majateam.bikespot.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Nicolas Martino on 08/06/15.
 */
public class Bike implements ClusterItem, Parcelable {

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

    public long getRawDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public Bike() {

    }

    @Override
    public LatLng getPosition() {
        return new LatLng(Double.valueOf(lat), Double.valueOf(lng));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeLong(date);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(brand);
    }

    public static final Parcelable.Creator<Bike> CREATOR = new Creator<Bike>() {

        public Bike createFromParcel(Parcel source) {

            Bike bike = new Bike();
            bike.id = source.readString();
            bike.lat = source.readString();
            bike.lng = source.readString();
            bike.date = source.readLong();
            bike.title = source.readString();
            bike.description = source.readString();
            bike.brand = source.readString();
            return bike;
        }

        @Override
        public Bike[] newArray(int size) {
            return new Bike[size];
        }
    };

}
