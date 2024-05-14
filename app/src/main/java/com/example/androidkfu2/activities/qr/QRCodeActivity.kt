package com.example.androidkfu2.activities.qr

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.androidkfu2.R
import com.example.androidkfu2.activities.qr.ui.theme.AndroidKfu2Theme
import qrcode.QRCode
import java.io.File

class QRCodeActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidKfu2Theme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    QRPage("Android")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun QRPage(name: String) {

    val context = LocalContext.current
    val ass = context.resources.assets
    val f = ass.open("logo.png")


    val a = QRCode.ofSquares().withLogo(f.readBytes(), 15, 15, true)
    val b = a.build(name).render().getBytes()
    val c = BitmapFactory.decodeByteArray(b, 0, b.size).asImageBitmap()

    Image(bitmap = c,
        contentDescription = null)

    Text(
        text = "Hello $name!"
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidKfu2Theme {
        QRPage("Android")
    }
}