package com.example.floathub

import android.content.Context
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {

        try {

            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))

            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())

            val log = """
                ===== CRASH LOG =====
                Date: $date
                Device: ${Build.MANUFACTURER} ${Build.MODEL}
                Android: ${Build.VERSION.RELEASE}

                ERROR:
                $sw

                =====================

            """.trimIndent()

            // ✅ écriture plus robuste
            val file = File(context.getExternalFilesDir(null), "crash_log.txt")

            FileOutputStream(file, true).use { fos ->
                fos.write(log.toByteArray())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        defaultHandler?.uncaughtException(thread, throwable)
    }
}
