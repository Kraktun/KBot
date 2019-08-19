package com.kraktun.kbot

import com.kraktun.kbot.bots.MainBot
import com.kraktun.kbot.utils.*
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

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

    ApiContextInitializer.init()
    val botsApi = TelegramBotsApi()
    try {
        botsApi.registerBot(MainBot(DefaultBotOptions()))
        onStart()
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}
