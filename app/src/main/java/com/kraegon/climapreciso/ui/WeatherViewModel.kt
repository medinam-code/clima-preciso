package com.kraegon.climapreciso.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kraegon.climapreciso.data.CurrentWeatherResponse
import com.kraegon.climapreciso.data.ForecastResponse
import com.kraegon.climapreciso.data.WeatherRepository
import com.kraegon.climapreciso.location.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    object EsperandoPermiso : WeatherUiState()
    data class Error(val mensaje: String) : WeatherUiState()
    data class Exito(
        val clima: CurrentWeatherResponse,
        val pronostico: ForecastResponse,
        val lat: Double,
        val lon: Double
    ) : WeatherUiState()
}

class WeatherViewModel(
    private val locationHelper: LocationHelper,
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.EsperandoPermiso)
    val uiState: StateFlow<WeatherUiState> = _uiState

    fun cargarClimaConGps() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val ubicacion = locationHelper.getPreciseLocation()
                if (ubicacion == null) {
                    _uiState.value = WeatherUiState.Error(
                        "No se pudo obtener la ubicación GPS. Verificá que el GPS esté activado y que haya señal."
                    )
                    return@launch
                }

                val clima = repository.fetchCurrentWeather(ubicacion.latitude, ubicacion.longitude)
                val pronostico = repository.fetchForecast(ubicacion.latitude, ubicacion.longitude)

                _uiState.value = WeatherUiState.Exito(
                    clima = clima,
                    pronostico = pronostico,
                    lat = ubicacion.latitude,
                    lon = ubicacion.longitude
                )
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("Error al cargar el clima: ${e.message}")
            }
        }
    }
}
