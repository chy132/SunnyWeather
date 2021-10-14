package com.sunnyweather.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.sunnyweather.logic.dao.PlaceDao
import com.sunnyweather.logic.model.Place
import com.sunnyweather.logic.model.PlaceResponse
import com.sunnyweather.logic.model.Weather
import com.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {
    fun savePlace(place:Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    fun searchPlaces(query:String) = liveData(Dispatchers.IO) {
        val result =
        try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if(placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }

        emit(result)
    }

    fun refreshWeather(lng:String,lat:String,placeName: String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                val deferredRealtime = async {
                    SunnyWeatherNetwork.getRealtimeWeather(lng,lat)
                }
                val deferredDaily = async {
                    SunnyWeatherNetwork.getDailyWeather(lng,lat)
                }
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if(realtimeResponse.status =="ok" && dailyResponse.status =="ok") {
                    val weather = Weather(realtimeResponse.result.realtime,dailyResponse.result.daily)
                    Result.success(weather)
                } else {
                    Result.failure(
                        RuntimeException(
                            "realtime response status is ${realtimeResponse.status} " +
                                    "daily response status is ${dailyResponse.status}"
                        )
                    )
                }
            }
        } catch (e:java.lang.Exception) {
            Result.failure<Weather>(e)
        }
        emit(result)
    }
}


