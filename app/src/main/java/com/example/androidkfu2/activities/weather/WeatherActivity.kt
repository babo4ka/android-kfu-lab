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

private enum class FetchStatus{
    NONE, FETCHING, FETCHED
}

@Composable
fun WeatherPage(userName: String){

    //city for weather
    var cityEntered by remember {
        mutableStateOf("")
    }

    //weather variables
    val weather = remember {
        mutableStateOf("")
    }

    val humidity = remember {
        mutableStateOf("")
    }

    val seaLevel = remember{
        mutableStateOf("")
    }

    //fetching info
    val fetched = remember {
        mutableStateOf(FetchStatus.NONE)
    }


    val scope = rememberCoroutineScope()

    val client = OkHttpClient()
    var responseString: String?
    val gson = Gson()

    fun fetchWeatherByCoord(weatherUrl: String) {
        fetched.value = FetchStatus.FETCHING
        val weatherRequest = Request.Builder()
            .url(weatherUrl)
            .build()

        client.newCall(weatherRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                responseString = response.body()?.string()
                val weatherAdapter = gson.getAdapter(object : TypeToken<Map<String, Any?>>() {})
                val weatherModel = weatherAdapter.fromJson(responseString)["main"] as? Map<*, *>

                if (weatherModel != null) {
                    val kel: Double = weatherModel["temp"].toString().toDouble()
                    weather.value = Math.round((kel - 273.15)).toString()
                    humidity.value = weatherModel["humidity"].toString()
                    seaLevel.value = weatherModel["sea_level"].toString()

                    fetched.value = FetchStatus.FETCHED
                }

                val weatherInfo = weatherAdapter.fromJson(responseString)["weather"] as? List<*>
                val weatherInfoMap = weatherInfo?.get(0) as? Map<*, *>
                println(weatherInfoMap?.get("icon"))
            }
        })
    }

    fun fetchWeatherByCityName(placeUrl: String){
        fetched.value = FetchStatus.FETCHING
        println(placeUrl)

        val placeRequest = Request.Builder()
            .url(placeUrl)
            .build()

        client.newCall(placeRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                responseString = response.body()?.string()

                val weatherAdapter = gson.getAdapter(object : TypeToken<Map<String, Any?>>() {})
                val weatherModel = weatherAdapter.fromJson(responseString)["main"] as? Map<*, *>

                if (weatherModel != null) {
                    val kel: Double = weatherModel["temp"].toString().toDouble()
                    weather.value = Math.round((kel - 273.15)).toString()
                    humidity.value = weatherModel["humidity"].toString()
                    seaLevel.value = weatherModel["sea_level"].toString()

                    fetched.value = FetchStatus.FETCHED
                }

                val weatherInfo = weatherAdapter.fromJson(responseString)["weather"] as? List<*>
                val weatherInfoMap = weatherInfo?.get(0) as? Map<*, *>
                println(weatherInfoMap?.get("icon"))
            }
        })
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
                    fetchWeatherByCityName("http://api.openweathermap.org/data/2.5/weather?q=${cityEntered},643&appid=6b84d2def543fb0ba85c2790ab5c400b")
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

            if(fetched.value == FetchStatus.FETCHED){
                Text(modifier = Modifier
                    .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,

                    text = "Weather: ${weather.value} °C \n" +
                            "Humidity: ${humidity.value} % \n" +
                            "Sea level: ${seaLevel.value}")
            }else if(fetched.value == FetchStatus.FETCHING){
                Text(modifier = Modifier
                    .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,

                    text = "Fetching information about weather...")
            }

        }
    }
}






@Composable
@Preview(showSystemUi = true, showBackground = true)
fun WeatherPagePreview(){
    WeatherPage("Eugene")
}