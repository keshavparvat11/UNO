package com.example.uno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.uno.screen.ClientGameScreen
import com.example.uno.screen.ClientLobbyScreen
import com.example.uno.screen.HostScreen
import com.example.uno.screen.JoinScreen
import com.example.uno.screen.MenuScreen
import com.example.uno.ui.theme.UNOTheme
import com.example.uno.viewModel.UnoViewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<UnoViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Hide status bar, navigation bar, and enable immersive mode
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            UNOTheme {
                var screen by remember { mutableStateOf("menu") }
                when (screen) {
                    "menu" -> MenuScreen(
                        onHost = {
                            viewModel.startHost(9999) // start hosting on port 9999
                            screen = "host"
                        },
                        onJoin = { screen = "join" }
                    )

                    "host" -> HostScreen(viewModel = viewModel)

                    "join" -> JoinScreen(onConnect = { ip, name ->
                        viewModel.connectToHost(
                            ip = ip,
                            name = name,
                            onJoined = {
                                screen = "client_lobby"
                            }
                        )
                    })

                    "client_lobby" -> ClientLobbyScreen(
                        viewModel = viewModel,
                        onEnterGame = { screen = "client_game" }
                    )

                    "client_game" -> ClientGameScreen(viewModel = viewModel)
                }

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UNOTheme {
        Greeting("Android")
    }
}