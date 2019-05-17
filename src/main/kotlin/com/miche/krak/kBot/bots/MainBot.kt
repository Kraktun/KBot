package com.miche.krak.kBot.bots

import com.miche.krak.kBot.*
import com.miche.krak.kBot.commands.*
import com.miche.krak.kBot.commands.core.CommandProcessor
import com.miche.krak.kBot.commands.core.BaseCommand
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Message


class MainBot(options: DefaultBotOptions) : TelegramLongPollingBot(options) {

    companion object {
        const val botName = TEST_NAME
    }

    override fun getBotUsername(): String? {
        return botName
    }

    override fun getBotToken(): String? {
        return TEST_TOKEN
    }

    /**
     * Register Commands Handlers
     */
    init {
        val commandProcessor = CommandProcessor.instance
        commandProcessor.registerCommand(HelloCommand().engine)
        commandProcessor.registerCommand(StartCommand().engine)
        commandProcessor.registerCommand(HelpCommand().engine)
        commandProcessor.registerCommand(LockCommand().engine)
        commandProcessor.registerCommand(UnlockCommand().engine)
    }

    /**
     * On update: fire commands if it's a recognized command, else manage in a different way
     */
    override fun onUpdateReceived(update: Update) {
        if (!CommandProcessor.instance.fireCommand(update, this)) {
            //Check if chat is locked or user is banned  and if so delete the message
            if (!BaseCommand.filterLock(update.message.from, update.message.chat) ||
                    !BaseCommand.filterStatus(update.message.from, update.message.chat)) {
                val message = DeleteMessage()
                                .setChatId(update.message.chatId)
                                .setMessageId(update.message.messageId)
                try {
                    execute(message)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }
            } else if (update.message.chat.isUserChat && update.hasMessage() && update.message.hasText()) {
                val message = SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.message.chatId)
                    .setText("I'm a parrot\n ${update.message.text}")
                try {
                    execute<Message, SendMessage>(message) // Call method to send the message
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }
            }
        }
    }
}