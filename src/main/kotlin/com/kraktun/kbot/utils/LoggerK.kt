package com.kraktun.kbot.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object LoggerK {

    private val fileHolder = File(getMainFolder().plus("/logs/log_").plus(getCurrentDateTimeStamp()).plus(".log"))
    @Volatile private var textHolder = StringBuilder()

    fun log(s: String) {
        synchronized(this) {
            textHolder.append(s + "\n")
        }
    }

    fun flush() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                synchronized(this) {
                    if (textHolder.isNotEmpty()) {
                        FileOutputStream(fileHolder, true).bufferedWriter().use {
                            it.write(textHolder.toString())
                            it.close()
                            textHolder = StringBuilder()
                        }
                    }
                }
            }
        }
    }
}