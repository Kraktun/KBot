package com.kraktun.kbot.bots

import com.kraktun.kbot.utils.username
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.meta.generics.WebhookBot

object BotsController {

    private val bots = mutableListOf<AbsSender>()
    private val botsApi = TelegramBotsApi()

    init {
        ApiContextInitializer.init()
    }

    fun initialize(bot: LongPollingBot) {
        try {
            botsApi.registerBot(bot)
            bots.add(bot as AbsSender)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun initialize(bot: WebhookBot) {
        try {
            botsApi.registerBot(bot)
            bots.add(bot as AbsSender)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun getBot(name: String): AbsSender? {
        return bots.find { it.username() == name }
    }
}