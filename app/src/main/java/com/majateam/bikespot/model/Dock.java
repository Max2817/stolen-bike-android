package com.majateam.bikespot.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Nicolas Martino on 08/06/15.
 */
public class Dock implements ClusterItem, Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lat);
        dest.writeString(lng);
    }

    public static final Parcelable.Creator<Dock> CREATOR = new Parcelable.Creator<Dock>() {

        public Dock createFromParcel(Parcel source) {

            Dock dock = new Dock();
            dock.lat = source.readString();
            dock.lng = source.readString();
            return dock;
        }

        @Override
        public Dock[] newArray(int size) {
            return new Dock[size];
        }
    };
}
