package com.miche.krak.kBot

import com.miche.krak.kBot.bots.MainBot
import com.miche.krak.kBot.utils.*
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

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
