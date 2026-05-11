package com.aircraftwar.utils

import android.util.Log

/**
 * Système de logging adapté à Android
 */
object GameLogger {
    private const val TAG = "AircraftWar"
    
    fun info(message: String) {
        println("ℹ️  $message")
        Log.i(TAG, message)
    }
    
    fun debug(message: String) {
        println("🔍 $message")
        Log.d(TAG, message)
    }
    
    fun warn(message: String) {
        println("⚠️  $message")
        Log.w(TAG, message)
    }
    
    fun error(message: String, throwable: Throwable? = null) {
        println("❌ $message")
        if (throwable != null) {
            Log.e(TAG, message, throwable)
        } else {
            Log.e(TAG, message)
        }
    }
}
