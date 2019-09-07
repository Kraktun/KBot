package com.kraktun.kbot.bots.ping

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object PingController {

    private lateinit var listener: PingListener
    @Volatile
    private var pingHolder = mutableListOf<Long>()
    private const val waitingTime = 10000L // 60 secs

    fun registerListener(listener: PingListener) {
        this.listener = listener
    }

    fun registerPing() {
        runBlocking {
            GlobalScope.launch {
                synchronized(this) {
                    pingHolder.add(System.nanoTime())
                }
                delay(waitingTime)
                synchronized(this) {
                    if (pingHolder.size > 0) {
                        if (pingHolder.first() + waitingTime < System.nanoTime()) {
                            pingHolder.removeAt(0)
                            listener.onPongTimeExceeded()
                        } // else do nothing
                    }
                }
            }
        }
    }

    fun registerPong() {
        synchronized(this) {
            if (pingHolder.size > 0) {
                pingHolder.removeAt(0)
            }
        }
    }
}