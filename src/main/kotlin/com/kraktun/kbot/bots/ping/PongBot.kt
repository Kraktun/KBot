package com.kraktun.kbot.bots.ping

import com.kraktun.kbot.*
import com.kraktun.kbot.commands.core.CommandProcessor
import com.kraktun.kbot.commands.private.RouterIpCommand
import com.kraktun.kbot.utils.simpleMessage
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

/**
 * Main class: register the commands and process non-command updates
 */
class PongBot(options: DefaultBotOptions) : TelegramLongPollingBot(options) {

    /**
     * Return username of the bot
     */
    override fun getBotUsername(): String? {
        return PING_RECEIVER_NAME
    }

    /**
     * Return token of the bot
     */
    override fun getBotToken(): String? {
        return PING_RECEIVER_TOKEN
    }

    init {
        CommandProcessor.registerCommand(botUsername!!, RouterIpCommand().engine)
    }

    /**
     * On update: check for pings and answer pong
     */
    override fun onUpdateReceived(update: Update) {
        val message = if (update.channelPost != null) update.channelPost else update.message
        if (message.hasText() && message.chat.id == PING_PONG_CHAT && message.text == PING)
            simpleMessage(this, PONG, message.chat)
        else
            CommandProcessor.fireCommand(message, this)
    }
}