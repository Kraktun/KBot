package com.kraktun.kbot

import com.kraktun.kbot.bots.BotsController
import com.kraktun.kbot.bots.PingBot
import com.kraktun.kbot.bots.MainBot
import com.kraktun.kbot.utils.*
import org.telegram.telegrambots.bots.DefaultBotOptions

// Necessary to access manifest variables and have a top level class (to get path)
// Better than having many single object{} I think
class Main

fun main() {
    val mainThread = Thread.currentThread()
    Runtime.getRuntime().addShutdownHook(Thread {
        onShutdown()
        try {
            mainThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    })

    BotsController.initialize(MainBot(DefaultBotOptions()))
    BotsController.initialize(PingBot(DefaultBotOptions()))
    onStart()
}
