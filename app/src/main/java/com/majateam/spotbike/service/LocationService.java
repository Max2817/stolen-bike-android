package com.majateam.spotbike.service;

import com.majateam.spotbike.model.Bike;
import com.majateam.spotbike.model.Dock;

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
