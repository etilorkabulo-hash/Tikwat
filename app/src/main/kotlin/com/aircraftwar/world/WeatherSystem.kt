package com.aircraftwar.world

import kotlin.math.sin

enum class WeatherType(val symbol: String) {
    CLEAR("☀️ Ciel Clair"),
    RAIN("🌧️ Pluie"),
    STORM("⛈️ Tempête"),
    FOG("🌫️ Brouillard"),
    SNOW("❄️ Neige")
}

class WeatherSystem(private val cycleDuration: Int = 1200) {
    
    private var timeCounter = 0
    var currentWeather = WeatherType.CLEAR
    
    var visibility = 1.0f
    var windForce = 0.0f
    var rainIntensity = 0.0f
    
    fun update() {
        timeCounter++
        
        val weatherPhase = (timeCounter % cycleDuration).toFloat() / cycleDuration * 360
        val weatherValue = sin(Math.toRadians(weatherPhase.toDouble())).toFloat()
        
        currentWeather = when {
            weatherValue < -0.6f -> WeatherType.STORM
            weatherValue < -0.2f -> WeatherType.RAIN
            weatherValue < 0.2f -> WeatherType.FOG
            weatherValue < 0.6f -> WeatherType.CLEAR
            else -> WeatherType.SNOW
        }
        
        visibility = when (currentWeather) {
            WeatherType.CLEAR -> 1.0f
            WeatherType.RAIN -> 0.85f
            WeatherType.STORM -> 0.6f
            WeatherType.FOG -> 0.4f
            WeatherType.SNOW -> 0.7f
        }
        
        windForce = sin(Math.toRadians(weatherPhase * 2)).toFloat() * when (currentWeather) {
            WeatherType.CLEAR -> 0.1f
            WeatherType.RAIN -> 0.3f
            WeatherType.STORM -> 0.8f
            WeatherType.FOG -> 0.15f
            WeatherType.SNOW -> 0.5f
        }
        
        rainIntensity = when (currentWeather) {
            WeatherType.RAIN -> 0.5f
            WeatherType.STORM -> 1.0f
            else -> 0.0f
        }
    }
    
    fun reset() {
        timeCounter = 0
        currentWeather = WeatherType.CLEAR
    }
}
