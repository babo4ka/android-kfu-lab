package com.example.androidkfu2.activities.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidkfu2.activities.InputField
import com.example.androidkfu2.activities.ui.theme.AndroidKfu2Theme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class WeatherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userName = intent.getStringExtra("userName")

        setContent {
            AndroidKfu2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (userName != null) {
                        WeatherPage(userName = userName)
                    }
                }
            }
        }
    }
}


@Composable
fun WeatherPage(userName: String){

    var cityEntered by remember {
        mutableStateOf("")
    }

    var weather = remember {
        mutableStateOf("")
    }


    var humidity = remember {
        mutableStateOf("")
    }

    var seaLevel = remember{
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()

    fun fetchPlace(placeUrl: String){
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
                val adapter = gson.getAdapter(object : TypeToken<List<Map<String, Any?>>>() {})
                val model = adapter.fromJson(resp)
                println("place resp: $resp")
                val lat = model[0]["lat"]
                val lon = model[0]["lon"]

                val weatherUrl =
                    "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=6b84d2def543fb0ba85c2790ab5c400b"

                val weatherRequest = Request.Builder()
                    .url(weatherUrl)
                    .build()

                client.newCall(weatherRequest).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("error ${e.message}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        println("weather on response: $weatherUrl")
                        val resp2 = response.body()?.string()
                        println("resp: $resp2")
                        val gson2 = Gson()
                        val adapter2 = gson2.getAdapter(object : TypeToken<Map<String, Any?>>() {})
                        var model2 = adapter2.fromJson(resp2)
                        println("model2 main: ${model2["main"].toString()}")
                        model2 = adapter2.fromJson(model2.get("main").toString())
                        println("hello")
                        println(model2)

                        val far: Double = model2["temp"].toString().toDouble()
                        weather.value = ((far - 32) / 1.8).toString()
                        humidity.value = model2["humidity"].toString()
                        seaLevel.value = model2["sea_level"].toString()

                        println("weather: ${model2["temp"].toString().toDouble()}")
                        println("humidity: ${model2["humidity"].toString()}")
                        println("sea level: ${model2["sea_level"].toString()}")
//
//                        println("weather: ${weather.value}")
//                        println("humidity: ${humidity.value}")
//                        println("sea level: ${seaLevel.value}")
                    }
                })
            }
        })

        println("end fetching...")
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .padding(28.dp)
        .clip(
            CutCornerShape(
                topStart = 8.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 8.dp
            )
        )
    ) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 48.dp, end = 48.dp, top = 48.dp)
        ) {
            Text(modifier = Modifier
                .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,

                text = "Weather in your city, $userName")

            Spacer(modifier = Modifier.height(80.dp))

            InputField(value = cityEntered,
                label = "City",
                placeholder = "Enter city",
                onValChange = {cityEntered = it},
                keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Text))

            Spacer(modifier = Modifier.height(80.dp))

            TextButton(onClick = {
                scope.launch {
                    fetchPlace("http://api.openweathermap.org/geo/1.0/direct?q=${cityEntered},643&appid=6b84d2def543fb0ba85c2790ab5c400b")
                }

            },
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(color = Color.Black)
                    .height(45.dp)
                    .width(200.dp)
            ) {
                Text(text = "Узнать погоду")
            }

            Spacer(modifier = Modifier.height(50.dp))

            Text(modifier = Modifier
                .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,

                text = "Weather: ${weather.value} \n" +
                        "Humidity: ${humidity.value} \n" +
                        "Sea level: ${seaLevel.value}")
        }
    }
}






@Composable
@Preview(showSystemUi = true, showBackground = true)
fun WeatherPagePreview(){
    WeatherPage("Eugene")
}