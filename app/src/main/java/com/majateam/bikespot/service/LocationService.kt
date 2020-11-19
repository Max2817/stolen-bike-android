package com.majateam.bikespot.service

import com.majateam.bikespot.model.Bike
import com.majateam.bikespot.model.Dock
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Stolen Bike Created by Nicolas Martino on 08/06/15.
 */
interface LocationService {
    @GET fun listBikes(@Url url: String?, @Query("added_after") addedAfter: String?): Call<List<Bike?>?>?
    @GET fun listDocks(@Url url: String?): Call<List<Dock?>?>?
}