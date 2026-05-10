package com.example.floathub

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import kotlin.math.abs

class FloatingView(
    private val context: Context,
    private val windowManager: WindowManager
) {

    private var containerView: LinearLayout? = null
    private val params = WindowManager.LayoutParams()
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    init {
        createFloatingView()
    }

    private fun createFloatingView() {
        // Container principal
        containerView = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        // Bouton WhatsApp
        val btnWhatsApp = Button(context).apply {
            text = "📱 WhatsApp"
            setBackgroundColor(android.graphics.Color.parseColor("#25D366"))
            setTextColor(android.graphics.Color.WHITE)
            textSize = 14f
            setPadding(25, 20, 25, 20)
            setOnClickListener {
                openWhatsApp()
            }
        }

        // Bouton TikTok
        val btnTikTok = Button(context).apply {
            text = "🎵 TikTok"
            setBackgroundColor(android.graphics.Color.parseColor("#000000"))
            setTextColor(android.graphics.Color.WHITE)
            textSize = 14f
            setPadding(25, 20, 25, 20)
            setOnClickListener {
                openTikTok()
            }
        }

        // Ajouter les boutons au container
        containerView!!.addView(btnWhatsApp)
        containerView!!.addView(btnTikTok)

        // Paramètres de la fenêtre flottante
        params.apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = android.graphics.PixelFormat.RGBA_8888
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.LEFT
            x = 0
            y = 100
        }

        try {
            windowManager.addView(containerView, params)
            Toast.makeText(context, "✅ Boutons flottants activés !", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "❌ Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun openWhatsApp() {
        try {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/")
            }
            
            // Chercher WhatsApp
            val activities = pm.queryIntentActivities(intent, 0)
            var whatsappFound = false
            
            for (activity in activities) {
                if (activity.activityInfo.packageName == "com.whatsapp" ||
                    activity.activityInfo.packageName == "com.whatsapp.w4b") {
                    intent.`package` = activity.activityInfo.packageName
                    whatsappFound = true
                    break
                }
            }
            
            if (whatsappFound) {
                context.startActivity(intent)
            } else {
                // Rediriger vers Play Store
                context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
                })
                Toast.makeText(context, "📥 Installe WhatsApp depuis Play Store", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "❌ Erreur WhatsApp : ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openTikTok() {
        try {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_VIEW)
            
            // Chercher TikTok
            val tiktokPackage = "com.ss.android.ugc.tiktok"
            val activities = pm.queryIntentActivities(intent, 0)
            var tiktokFound = false
            
            for (activity in activities) {
                if (activity.activityInfo.packageName == tiktokPackage) {
                    intent.data = Uri.parse("https://www.tiktok.com")
                    intent.`package` = tiktokPackage
                    tiktokFound = true
                    break
                }
            }
            
            if (tiktokFound) {
                context.startActivity(intent)
            } else {
                // Rediriger vers Play Store
                context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=$tiktokPackage")
                })
                Toast.makeText(context, "📥 Installe TikTok depuis Play Store", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "❌ Erreur TikTok : ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeView() {
        try {
            if (containerView != null) {
                windowManager.removeView(containerView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
