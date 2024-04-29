package com.example.androidkfu2.activities.weather

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class WeatherData {
    var weather : String = ""
        set(value){
            field = value
        }
        get(){
            return field
        }

    var humidity : String = ""
        set(value){
            field = value
        }
        get(){
            return field
        }

    var seaLevel : String = ""
        set(value){
            field = value
        }
        get(){
            return field
        }

    var weatherUrl : String = ""
        set(value){
            field = value
        }
        get(){
            return field
        }
    var placeUrl : String = ""
        set(value){
            field = value
        }
        get(){
            return field
        }


    suspend fun getWeather() = coroutineScope{
        val a = launch {fetchPlace()}
        a.join()
    }

     private suspend fun fetchPlace() = coroutineScope{
        println("fetching place")
        val placeRequest = Request.Builder()
            .url(placeUrl)
            .build()

        val client = OkHttpClient()

        client.newCall(placeRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                println("place on response")
                val resp = response.body()?.string()
                val gson = Gson()
                val adapter = gson.getAdapter(object: TypeToken<List<Map<String, Any?>>>(){})
                val model = adapter.fromJson(resp)

                val lat = model[0]["lat"]
                val lon = model[0]["lon"]

                weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=6b84d2def543fb0ba85c2790ab5c400b"

                val weatherRequest = Request.Builder()
                    .url(weatherUrl)
                    .build()

                client.newCall(weatherRequest).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("error ${e.message}")
                    }
                    override fun onResponse(call: Call, response: Response) {
                        println("weather on response")
                        val resp = response.body()?.string()
                        val gson = Gson()
                        val adapter = gson.getAdapter(object: TypeToken<Map<String, Any?>>(){})
                        var model = adapter.fromJson(resp)
                        model = adapter.fromJson(model["main"].toString())

                        val far: Double = model["temp"].toString().toDouble()
                        weather = ((far-32)/1.8).toString()
                        humidity = model["humidity"].toString()
                        seaLevel = model["sea_level"].toString()
                    }
                })
            }
        })
    }


    private fun fetchWeather(){
        println("fetching weather")
        val request = Request.Builder()
            .url(weatherUrl)
            .build()

        val client = OkHttpClient()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                println("weather on response")
                val resp = response.body()?.string()
                val gson = Gson()
                val adapter = gson.getAdapter(object: TypeToken<Map<String, Any?>>(){})
                var model = adapter.fromJson(resp)
                model = adapter.fromJson(model["main"].toString())

                val far: Double = model["temp"].toString().toDouble()
                weather = ((far-32)/1.8).toString()
                humidity = model["humidity"].toString()
                seaLevel = model["sea_level"].toString()

            }
        })
    }

}