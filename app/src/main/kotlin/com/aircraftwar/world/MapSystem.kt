package com.aircraftwar.world

import com.aircraftwar.utils.GameLogger
import kotlin.random.Random

enum class TerrainType(val symbol: String, val difficulty: Float) {
    PLAIN("🌾", 1.0f),
    MOUNTAIN("⛰️", 1.3f),
    DESERT("🏜️", 1.1f),
    OCEAN("🌊", 1.5f),
    FOREST("🌲", 1.2f),
    VOLCANIC("🌋", 1.4f)
}

class MapSystem(val screenWidth: Int = 400, val screenHeight: Int = 800) {
    
    private var updateCounter = 0
    private val updateInterval = 180
    
    var currentTerrain = TerrainType.PLAIN
    var timeOfDay = TimeOfDay.DAY
    
    fun update(weatherSystem: WeatherSystem) {
        updateCounter++
        
        if (updateCounter >= updateInterval) {
            changeTerrainRandomly()
            updateTimeOfDay()
            updateCounter = 0
        }
    }
    
    private fun changeTerrainRandomly() {
        val newTerrain = TerrainType.values().random()
        if (newTerrain != currentTerrain) {
            currentTerrain = newTerrain
            GameLogger.info("🗺️ Changement: ${currentTerrain.symbol}")
        }
    }
    
    private fun updateTimeOfDay() {
        val newTime = listOf(TimeOfDay.DAY, TimeOfDay.SUNSET, TimeOfDay.NIGHT, TimeOfDay.SUNRISE).random()
        if (newTime != timeOfDay) {
            timeOfDay = newTime
            GameLogger.info("🕐 ${timeOfDay.symbol}")
        }
    }
    
    fun reset() {
        updateCounter = 0
        currentTerrain = TerrainType.PLAIN
        timeOfDay = TimeOfDay.DAY
    }
}

enum class TimeOfDay(val symbol: String) {
    DAY("☀️"), SUNSET("🌅"), NIGHT("🌙"), SUNRISE("🌄")
}
