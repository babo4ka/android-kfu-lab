package com.example.androidkfu2.activities.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
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
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager


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

fun disableSSLVerification() {
    val trustAllCerts = arrayOf(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })

    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
}




private enum class FetchStatus{
    NONE, FETCHING, FETCHED
}

private val appKey = "6b84d2def543fb0ba85c2790ab5c400b"


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherPage(userName: String){
    disableSSLVerification()
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

    val icon = remember{
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

    val context = LocalContext.current

    val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }

    val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            println("location ${location.latitude} : ${location.longitude}")
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun buildIconStr(iconName:String):String{
        return "https://openweathermap.org/img/wn/$iconName.png"
    }

    fun parseWeather(responseString: String?){
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
        icon.value = buildIconStr(weatherInfoMap?.get("icon").toString())
        println(weatherInfoMap?.get("icon"))
    }

    fun fetchWeatherByCoord() {
        fetched.value = FetchStatus.FETCHING
        val location = locationManager.getLastKnownLocation("gps")

        val weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat=${location?.latitude}&lon=${location?.longitude}&appid=$appKey"

        val weatherRequest = Request.Builder()
            .url(weatherUrl)
            .build()

        client.newCall(weatherRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                responseString = response.body?.string()
                parseWeather(responseString)
            }
        })
    }

    fun fetchWeatherByCityName(placeUrl: String){
        fetched.value = FetchStatus.FETCHING
        println(placeUrl)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        val placeRequest = Request.Builder()
            .url(placeUrl)
            .build()

        client.newCall(placeRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                responseString = response.body?.string()
                parseWeather(responseString)
            }
        })
    }



    Box(modifier = Modifier
        .fillMaxSize()
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
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
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
                    fetchWeatherByCityName("http://api.openweathermap.org/data/2.5/weather?q=${cityEntered},643&appid=$appKey")
                }

            },
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(color = Color.Black)
                    .height(45.dp)
                    .width(200.dp)
            ) {
                Text(text = "Get weather by city name")
            }

            Spacer(modifier = Modifier.height(15.dp))
            
            TextButton(onClick = {
                scope.launch {
                    fetchWeatherByCoord()
                }

            },
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(color = Color.Black)
                    .height(45.dp)
                    .width(200.dp)
            ) {
                Text(text = "Get weather by location")
            }

            Spacer(modifier = Modifier.height(50.dp))

            if(fetched.value == FetchStatus.FETCHED){
                Box(modifier = Modifier
                    .fillMaxSize()
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
                    ) {
                        Text(modifier = Modifier
                            .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp,

                            text = "Weather: ${weather.value} °C \n" +
                                    "Humidity: ${humidity.value} % \n" +
                                    "Sea level: ${seaLevel.value}")

                        Spacer(modifier = Modifier.height(50.dp))

//                        AsyncImage(
//                            model = icon.value,
//                            error = painterResource(id = R.drawable.logo),
//                            contentDescription = null,
//                            contentScale = ContentScale.Fit,
//                            modifier = Modifier.fillMaxSize()
//                        )

                        GlideImage(
                            model = icon.value,
                            contentDescription = null)
                    }
                }
            }
            else if(fetched.value == FetchStatus.FETCHING){
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