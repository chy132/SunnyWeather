package com.sunnyweather.logic.network

import retrofit2.http.Query
import com.sunnyweather.SunnyWeatherApplication
import com.sunnyweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET

interface PlaceService {

    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>

}