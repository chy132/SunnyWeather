package com.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sunnyweather.SunnyWeatherApplication
import com.sunnyweather.logic.model.Place

object PlaceDao {
    private fun sharedPreferences() = SunnyWeatherApplication.context.
            getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

    fun savePlace(place:Place) {
        sharedPreferences().edit {
            putString("place",Gson().toJson(place))
        }
    }

    fun getPlace():Place {
        val placeJson = sharedPreferences().getString("place","")
        return Gson().fromJson(placeJson,Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")
}