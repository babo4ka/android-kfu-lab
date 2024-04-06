package com.example.androidkfu2

import android.graphics.pdf.PdfDocument.Page
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidkfu2.ui.theme.AndroidKfu2Theme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidKfu2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainPage()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainPage(){

    val pagerState = rememberPagerState (pageCount = {2})
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxSize()
    ){
        Column(
            Modifier
                .fillMaxSize()) {

            Image(painter = painterResource(id = R.drawable.logo),
                contentDescription = null)

            Spacer(modifier = Modifier.height(50.dp))

            val tabs = listOf("Log in", "Registration")
            var tabIndex by remember { mutableIntStateOf(0) }

            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed{ index, title ->
                    Tab(selected = tabIndex == index,
                        onClick = {
                            tabIndex = index
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }) {
                        Text(text = title)
                    }
                }
            }

            HorizontalPager(state = pagerState) {
                    page ->
                        when(page){
                            0->{
                                LoginPage()
                                tabIndex = 0
                            }
                            1->{
                                RegistrationPage()
                                tabIndex = 1
                            }
                        }
            }
        }
    }
}


@Composable
fun LoginPage(){

    var userName by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
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
        )){
        Column(
            Modifier
                .fillMaxSize()
                .padding(48.dp)) {


            InputField(value = userName,
                label = "Username",
                placeholder = "Enter username",
                onValChange = {
                    userName = it
                })

            InputField(value = password,
                label = "Password",
                placeholder = "Enter password",
                onValChange = {
                    password = it
                })

            Spacer(Modifier.height(10.dp))

            TextButton(onClick = { /*TODO*/ },
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(color = Color.Black)
                    .height(45.dp)
                    .width(100.dp)
            ) {
                Text(text = "Войти")
            }

        }
    }
}


@Composable
fun RegistrationPage(){
    var userName by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
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
    ){
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 48.dp, end = 48.dp, top = 48.dp)
        ) {


            InputField(value = userName,
                label = "Username",
                placeholder = "Create username",
                onValChange = {
                    userName = it
                })

            InputField(value = password,
                label = "Password",
                placeholder = "Create password",
                onValChange = {
                    password = it
                })

            InputField(value = password,
                label = "Confirm password",
                placeholder = "Confirm password",
                onValChange = {
                    password = it
                })

            Spacer(Modifier.height(10.dp))

            TextButton(onClick = { /*TODO*/ },
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(color = Color.Black)
                    .height(45.dp)
                    .width(200.dp)
            ) {
                Text(text = "Создать аккаунт")
            }
        }
    }
}


@Composable
fun InputField(
    value: String,
    label: String,
    placeholder: String,
    onValChange: (String) -> Unit
){
    OutlinedTextField(
        value = value,
        onValueChange = {onValChange(it.take(18))},
        label =
        {
            Text(text = label)
        },
        placeholder = {
            Text(text = placeholder)
        },
        singleLine = true
    )
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LoginPagePreview(){
    LoginPage()
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RegistrationPagePreview(){
    RegistrationPage()
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MainPagePreview(){
    MainPage()
}