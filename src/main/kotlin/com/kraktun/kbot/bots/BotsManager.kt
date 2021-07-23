package com.kraktun.kbot.bots

import com.kraktun.kbot.utils.username
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.meta.generics.WebhookBot
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

/**
 * Maps bots username with corresponding absSender object
 */
object BotsManager {

    private val bots = mutableListOf<AbsSender>()
    private val botsApi = TelegramBotsApi(DefaultBotSession::class.java)

    fun registerBot(bot: LongPollingBot): BotsManager {
        try {
            botsApi.registerBot(bot)
            bots.add(bot as AbsSender)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
        return this
    }

    fun registerBot(bot: WebhookBot, setWebHook: SetWebhook): BotsManager {
        try {
            botsApi.registerBot(bot, setWebHook)
            bots.add(bot as AbsSender)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
        return this
    }

    fun getByUsername(name: String): AbsSender? {
        return bots.find { it.username() == name }
    }
}
