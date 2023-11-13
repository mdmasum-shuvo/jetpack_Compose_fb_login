package com.appinion.fb

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.appinion.fb.ui.theme.MyApplicationTheme
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Greeting("Android")
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FacebookButton()
                    }

                }
            }
        }
    }
}

@Composable
fun FacebookButton(
    modifier: Modifier = Modifier,
) {
    var imgUrl = remember {
        mutableStateOf("")
    }
    var phone = remember {
        mutableStateOf("")
    }
    var name = remember {
        mutableStateOf("")
    }
    val loginManager = LoginManager.getInstance()
    val callbackManager = remember { CallbackManager.Factory.create() }
    val launcher = rememberLauncherForActivityResult(
        loginManager.createLogInActivityResultContract(callbackManager, null)
    ) {
        // nothing to do. handled in FacebookCallback
    }
    Greeting(imgUrl.value, name.value, phone.value)
    DisposableEffect(Unit) {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                // do nothing

            }

            override fun onError(error: FacebookException) {
                Log.e("facebook", "on error call$error")
            }

            override fun onSuccess(result: LoginResult) {
                Log.e("facebook", "on Success call$result")
                val request = GraphRequest.newMeRequest(
                    result.accessToken
                ) { obj, response ->
                    Log.e("", "")
                    Log.e("", "")

                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,link")
                request.parameters = parameters
                request.executeAsync()
            }
        })

        onDispose {
            loginManager.unregisterCallback(callbackManager)
        }
    }
    Button(
        modifier = modifier,
        onClick = {
            // start the sign-in flow
            launcher.launch(listOf("email", "public_profile"))
        }) {
        Text("Continue with Facebook")
    }
}

@Composable
fun Greeting(imgUrl: String, name: String, phone: String) {

    val context = LocalContext.current
    Column {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imgUrl)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .build(), contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(100.dp)
        )
        Text(text = name)
        Text(text = phone)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        // Greeting("Android")
    }
}