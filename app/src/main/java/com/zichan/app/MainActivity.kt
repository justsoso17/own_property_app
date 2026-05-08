package com.zichan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zichan.app.data.database.SeedData
import com.zichan.app.ui.navigation.ZichanNavHost
import com.zichan.app.ui.theme.ZichanTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var seedData: SeedData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch { seedData.seed() }
        enableEdgeToEdge()
        setContent {
            ZichanTheme {
                ZichanNavHost()
            }
        }
    }
}
