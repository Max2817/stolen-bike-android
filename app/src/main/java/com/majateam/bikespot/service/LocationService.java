package com.majateam.bikespot.service;

import com.majateam.bikespot.model.Bike;
import com.majateam.bikespot.model.Dock;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Stolen Bike Created by Nicolas Martino on 08/06/15.
 */
public interface LocationService {

    @GET
    Call<List<Bike>> listBikes(@Url String url, @Query("added_after") String addedAfter);

    @GET
    Call<List<Dock>> listDocks(@Url String url);


}
