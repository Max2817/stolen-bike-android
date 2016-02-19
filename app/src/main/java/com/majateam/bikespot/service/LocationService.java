package com.majateam.bikespot.service;

import com.majateam.bikespot.model.Bike;
import com.majateam.bikespot.model.Dock;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Url;

/**
 * Created by Nicolas Martino on 08/06/15.
 */
public interface LocationService {

        @GET
        Call<List<Bike>> listBikes(@Url String url);
        @GET
        Call<List<Dock>> listDocks(@Url String url);




}
