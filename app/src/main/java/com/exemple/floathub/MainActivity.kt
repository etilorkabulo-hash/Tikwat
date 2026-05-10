package com.example.floathub

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ IMPORTANT : capture les crashs
        Thread.setDefaultUncaughtExceptionHandler(
            CrashHandler(this)
        )

        setContentView(R.layout.activity_main)

        val btnStart = findViewById<Button>(R.id.btn_start)
        val btnStop = findViewById<Button>(R.id.btn_stop)

        btnStart.setOnClickListener {
            checkPermissionAndStart()
        }

        btnStop.setOnClickListener {
            stopFloatingService()
        }
    }

    private fun checkPermissionAndStart() {

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (Settings.canDrawOverlays(this)) {
                    startFloatingService()
                } else {
                    requestOverlayPermission()
                }

            } else {
                startFloatingService()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Erreur permission: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun startFloatingService() {

        try {

            if (!Settings.canDrawOverlays(this)) {
                requestOverlayPermission()
                return
            }

            val intent = Intent(this, FloatingService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }

            Toast.makeText(
                this,
                "✅ Service démarré",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "❌ Erreur service: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun stopFloatingService() {

        try {

            val intent = Intent(this, FloatingService::class.java)
            stopService(intent)

            Toast.makeText(
                this,
                "❌ Service arrêté",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestOverlayPermission() {

        try {

            Toast.makeText(
                this,
                "⚠️ Autorise l'affichage par-dessus les autres apps",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )

            startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
