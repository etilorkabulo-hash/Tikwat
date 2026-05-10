package com.example.floathub

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import androidx.core.app.NotificationCompat

class FloatingService : Service() {

    private var floatingView: FloatingView? = null
    private lateinit var windowManager: WindowManager

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatingView = FloatingView(this, windowManager)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotification()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.removeView()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification() {
        val notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("🎯 Floating Hub Actif")
            .setContentText("WhatsApp & TikTok au bout de tes doigts")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }
}
