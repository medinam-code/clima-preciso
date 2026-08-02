package com.kraegon.climapreciso.ui

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.kraegon.climapreciso.data.ForecastItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(locationPermission.status) {
        if (locationPermission.status.isGranted) {
            viewModel.cargarClimaConGps()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Clima Preciso") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                !locationPermission.status.isGranted -> {
                    PermisoUbicacion(
                        mostrarRationale = locationPermission.status.shouldShowRationale,
                        onSolicitar = { locationPermission.launchPermissionRequest() }
                    )
                }
                uiState is WeatherUiState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Obteniendo tu ubicación y el clima...")
                    }
                }
                uiState is WeatherUiState.Error -> {
                    val error = (uiState as WeatherUiState.Error).mensaje
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚠️ $error", modifier = Modifier.padding(24.dp))
                        Button(onClick = { viewModel.cargarClimaConGps() }) {
                            Text("Reintentar")
                        }
                    }
                }
                uiState is WeatherUiState.Exito -> {
                    val exito = uiState as WeatherUiState.Exito
                    ContenidoClima(exito, onRefrescar = { viewModel.cargarClimaConGps() })
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun PermisoUbicacion(mostrarRationale: Boolean, onSolicitar: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            if (mostrarRationale)
                "Necesitamos acceso a tu ubicación GPS para darte el clima exacto de donde estás."
            else
                "Esta app usa tu GPS para mostrar el clima preciso de tu ubicación actual.",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onSolicitar) {
            Text("Permitir ubicación")
        }
    }
}

@Composable
private fun ContenidoClima(exito: WeatherUiState.Exito, onRefrescar: () -> Unit) {
    val clima = exito.clima
    val descripcion = clima.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(clima.name.ifBlank { "Tu ubicación" }, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(descripcion, fontSize = 16.sp)
        Spacer(Modifier.height(16.dp))
        Text("${clima.main.temp.toInt()}°C", fontSize = 56.sp, fontWeight = FontWeight.Bold)
        Text("Sensación térmica: ${clima.main.feelsLike.toInt()}°C")

        Spacer(Modifier.height(20.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                FilaDato("Humedad", "${clima.main.humidity}%")
                FilaDato("Presión", "${clima.main.pressure} hPa")
                FilaDato("Viento", "${clima.wind.speed} m/s")
                FilaDato("Mín / Máx", "${clima.main.tempMin.toInt()}° / ${clima.main.tempMax.toInt()}°")
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Próximas horas", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(8.dp))
        exito.pronostico.list.take(8).forEach { item ->
            FilaPronostico(item)
        }

        Spacer(Modifier.height(20.dp))
        OutlinedButton(onClick = onRefrescar) {
            Text("Actualizar")
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta)
        Text(valor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FilaPronostico(item: ForecastItem) {
    val hora = remember(item.dt) {
        SimpleDateFormat("HH:mm", Locale("es")).format(Date(item.dt * 1000))
    }
    val desc = item.weather.firstOrNull()?.main ?: ""
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(hora)
        Text(desc)
        Text("${item.main.temp.toInt()}°C")
        Text("${(item.pop * 100).toInt()}% lluvia")
    }
}
