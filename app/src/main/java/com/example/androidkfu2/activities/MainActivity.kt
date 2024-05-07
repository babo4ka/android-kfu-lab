package com.example.androidkfu2.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import com.example.androidkfu2.R
import com.example.androidkfu2.activities.weather.WeatherActivity
import com.example.androidkfu2.database.databases.UsersDatabase
import com.example.androidkfu2.database.view_models.UserViewModel
import com.example.androidkfu2.database.view_models.factories.UserViewModelFactory
import com.example.androidkfu2.ui.theme.AndroidKfu2Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = UsersDatabase.getInstance(application).userDao
        val viewModelFactory = UserViewModelFactory(dao)
        val uvm = ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)

        setContent {
            AndroidKfu2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainPage(uvm = uvm)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainPage(uvm: UserViewModel?){

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
                                LoginPage(uvm= uvm)
                                tabIndex = 0
                            }
                            1->{
                                RegistrationPage(uvm= uvm)
                                tabIndex = 1
                            }
                        }
            }
        }
    }
}


@Composable
fun LoginPage(uvm: UserViewModel?){

    var userName by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValChange = {
                    userName = it
                })

            InputField(value = password,
                label = "Password",
                placeholder = "Enter password",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValChange = {
                    password = it
                })

            Spacer(Modifier.height(10.dp))

            TextButton(onClick = {
                scope.launch {
                    val u = uvm?.getUser(userName)
                    if(u == null){
                        val toast = Toast.makeText(context, "No account with entered username",
                            Toast.LENGTH_SHORT)
                        toast.show()
                    }else{
                        if(u.password != password){
                            val toast = Toast.makeText(context, "Incorrect password!!",
                                Toast.LENGTH_SHORT)
                            toast.show()
                        }else{
                            val intent = Intent(context, WeatherActivity::class.java)
                            intent.putExtra("userName", userName)
                            startActivity(context, intent, null)
                        }
                    }
                }

            },
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
fun RegistrationPage(uvm: UserViewModel?){
    var userName by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValChange = {
                    userName = it
                })

            InputField(value = password,
                label = "Password",
                placeholder = "Create password",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValChange = {
                    password = it
                })

            InputField(value = confirmPassword,
                label = "Confirm password",
                placeholder = "Confirm password",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValChange = {
                    confirmPassword = it
                })

            Spacer(Modifier.height(10.dp))

            TextButton(onClick = {
                scope.launch {
                    val u = uvm?.getUser(userName)
                    if (u != null){
                        val toast = Toast.makeText(context, "User $userName already exists", Toast.LENGTH_SHORT)
                        toast.show()
                    }else{
                        if(password != confirmPassword){
                            val toast = Toast.makeText(context, "Passwords aren't match", Toast.LENGTH_SHORT)
                            toast.show()
                        }else{
                            uvm?.addUser(userName, password)
                            val intent = Intent(context, GreetingActivity::class.java)
                            intent.putExtra("userName", userName)
                            startActivity(context, intent, null)
                        }
                    }
                }

            },
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
    onValChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions
){
    OutlinedTextField(
        keyboardOptions = keyboardOptions,
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
    LoginPage(null)
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RegistrationPagePreview(){
    RegistrationPage(null)
}


@Preview
@Composable
fun MainPagePreview(){
    MainPage(null)
}