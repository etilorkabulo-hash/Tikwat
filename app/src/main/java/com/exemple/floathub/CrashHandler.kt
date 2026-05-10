package com.example.floathub

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class CrashHandler(private val context: Context) :
    Thread.UncaughtExceptionHandler {

    private val defaultHandler =
        Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(
        thread: Thread,
        throwable: Throwable
    ) {

        try {

            val sw = StringWriter()
            val pw = PrintWriter(sw)

            throwable.printStackTrace(pw)

            val crashText = sw.toString()

            val file = File(
                context.filesDir,
                "crash_log.txt"
            )

            file.writeText(crashText)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        defaultHandler?.uncaughtException(thread, throwable)
    }
    }
