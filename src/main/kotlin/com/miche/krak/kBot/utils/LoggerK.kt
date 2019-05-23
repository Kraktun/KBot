package com.miche.krak.kBot.utils

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class LoggerK private constructor(){

    companion object {
        val instance by lazy {LoggerK()}
    }

    private val fileHolder = File(getMainFolder().plus("/logs/log_").plus(getCurrentDateTimeStamp()).plus(".log"))
    private var textHolder = StringBuilder()
    private val lock = ReentrantLock()

    fun log(s : String) {
        lock.withLock {
            textHolder.append(s + "\n")
        }
    }

    fun flush() {
        Thread().run {
            lock.withLock {
                val outStream = FileOutputStream(fileHolder, true)
                val buffw = OutputStreamWriter(outStream, "UTF-8")
                try {
                    buffw.write(textHolder.toString())
                    textHolder = StringBuilder()
                } finally {
                    buffw.close()
                    outStream.close()
                }
            }
        }
    }
}