package com.majateam.bikespot.service;

import com.majateam.bikespot.model.Bike;
import com.majateam.bikespot.model.Dock;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Nicolas Martino on 08/06/15.
 */
public interface LocationService {

        @GET("/bikes")
        void listBikes(Callback<List<Bike>> cb);
        @GET("/docks")
        void listDocks(Callback<List<Dock>> cb);




}
