package com.kraegon.climapreciso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kraegon.climapreciso.data.WeatherRepository
import com.kraegon.climapreciso.location.LocationHelper
import com.kraegon.climapreciso.ui.WeatherScreen
import com.kraegon.climapreciso.ui.WeatherViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationHelper = LocationHelper(applicationContext)
        val repository = WeatherRepository(apiKey = BuildConfig.OWM_API_KEY)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: WeatherViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return WeatherViewModel(locationHelper, repository) as T
                            }
                        }
                    )
                    WeatherScreen(viewModel = viewModel)
                }
            }
        }
    }
}
