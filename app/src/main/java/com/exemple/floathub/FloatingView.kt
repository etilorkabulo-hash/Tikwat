package com.example.floathub

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

class FloatingView(
    private val context: Context,
    private val windowManager: WindowManager
) {

    private var containerView: LinearLayout? = null

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
            setPadding(20, 20, 20, 20)
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
            setPadding(20, 20, 20, 20)
            setOnClickListener {
                openTikTok()
            }
        }

        // Ajouter les boutons
        containerView!!.addView(btnWhatsApp)
        containerView!!.addView(btnTikTok)

        // Paramètres de la fenêtre
        val params = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = android.graphics.PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.BOTTOM or Gravity.END
            x = 50
            y = 50
        }

        windowManager.addView(containerView, params)
    }

    private fun openWhatsApp() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/")
                `package` = "com.whatsapp"
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "❌ WhatsApp non installé", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openTikTok() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.tiktok.com")
                `package` = "com.ss.android.ugc.tiktok"
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "❌ TikTok non installé", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeView() {
        if (containerView != null) {
            try {
                windowManager.removeView(containerView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
