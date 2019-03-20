package com.miche.krak.kBot.bots

import com.miche.krak.kBot.*
import com.miche.krak.kBot.commands.core.CommandProcessor
import com.miche.krak.kBot.commands.HelloCommand
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message


class MainBot(options: DefaultBotOptions) : TelegramLongPollingBot(options) {

    override fun getBotUsername(): String? {
        return TEST_NAME
    }

    override fun getBotToken(): String? {
        return TEST_TOKEN
    }

    /**
     * Register Commands Handlers
     */
    init {
        CommandProcessor.instance.registerCommand(HelloCommand().engine)
    }

    /**
     * On update: fire commands if it's a recognized command, else manage in a different way
     */
    override fun onUpdateReceived(update: Update) {
        if (!CommandProcessor.instance.fireCommand(update, this)) {
            if (update.hasMessage() && update.message.hasText()) {
                val message = SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.message.chatId)
                    .setText(update.message.text)
                try {
                    execute<Message, SendMessage>(message) // Call method to send the message
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }

            }
        }
    }
}