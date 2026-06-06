package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ChiomaViewModel
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ConnectScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: ChiomaViewModel = viewModel()
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = androidx.compose.ui.graphics.Color(0xFF0F0F1A)
        ) {
          Crossfade(
            targetState = viewModel.currentScreen,
            label = "ScreenTransition"
          ) { screen ->
            when (screen) {
              "WelcomeScreen" -> WelcomeScreen(viewModel)
              "ConnectScreen" -> ConnectScreen(viewModel)
              "ChatScreen" -> ChatScreen(viewModel)
              "ProfileScreen" -> ProfileScreen(viewModel)
              else -> WelcomeScreen(viewModel)
            }
          }
        }
      }
    }
  }
}

