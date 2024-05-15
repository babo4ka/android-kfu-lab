package com.example.androidkfu2.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize

class WeatherWidget: GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            GlanceTheme {
                MyContent()
            }
        }
    }

}

@Composable
private fun MyContent() {
    Box(modifier = GlanceModifier.fillMaxSize()
    ){
        Column(modifier = GlanceModifier.fillMaxSize()) {
            Button(text = "Обновить", onClick = { println("gello") })
        }
        //Text(text = "Where to?", modifier = GlanceModifier.padding(12.dp))
    }

}
