package com.kraegon.climapreciso.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class WeatherRepository(private val apiKey: String) {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api = retrofit.create(WeatherApiService::class.java)

    suspend fun fetchCurrentWeather(lat: Double, lon: Double): CurrentWeatherResponse {
        return api.getCurrentWeather(lat = lat, lon = lon, apiKey = apiKey)
    }

    suspend fun fetchForecast(lat: Double, lon: Double): ForecastResponse {
        return api.getForecast(lat = lat, lon = lon, apiKey = apiKey)
    }
}
