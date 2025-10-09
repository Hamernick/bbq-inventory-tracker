package com.bbqreset

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.bbqreset.ui.design.BBQTheme
import com.bbqreset.ui.screens.TodayScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BBQApp()
        }
    }
}

@Composable
fun BBQApp() {
    BBQTheme {
        Surface {
            TodayScreen()
        }
    }
}
