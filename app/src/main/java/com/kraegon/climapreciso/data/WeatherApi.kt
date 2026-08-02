package com.kraegon.climapreciso.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class CurrentWeatherResponse(
    val name: String,
    val main: MainData,
    val weather: List<WeatherDesc>,
    val wind: WindData,
    val visibility: Int? = null,
    val dt: Long
)

@JsonClass(generateAdapter = true)
data class MainData(
    val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "temp_min") val tempMin: Double,
    @Json(name = "temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int
)

@JsonClass(generateAdapter = true)
data class WindData(
    val speed: Double,
    val deg: Int
)

@JsonClass(generateAdapter = true)
data class WeatherDesc(
    val main: String,
    val description: String,
    val icon: String
)

@JsonClass(generateAdapter = true)
data class ForecastResponse(
    val list: List<ForecastItem>
)

@JsonClass(generateAdapter = true)
data class ForecastItem(
    val dt: Long,
    val main: MainData,
    val weather: List<WeatherDesc>,
    val pop: Double
)

interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): CurrentWeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): ForecastResponse
}
