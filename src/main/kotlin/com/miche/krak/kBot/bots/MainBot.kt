package com.miche.krak.kBot.bots

import com.miche.krak.kBot.*
import com.miche.krak.kBot.commands.*
import com.miche.krak.kBot.commands.core.CommandProcessor
import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.MultiCommandsHandler
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * Main class: register the commands and process non-command updates
 */
class MainBot(options: DefaultBotOptions) : TelegramLongPollingBot(options) {

    /*
     * Same as getBotUSername(), must be esplicit so that the bot can process commands in groups in the form /hello@botaName
     */
    companion object {
        const val botName = TEST_NAME
    }

    /**
     * Return username of the bot
     */
    override fun getBotUsername(): String? {
        return botName
    }

    /**
     * Return token of the bot
     */
    override fun getBotToken(): String? {
        return TEST_TOKEN
    }

    /**
     * Register Commands
     */
    init {
        val commandProcessor = CommandProcessor.instance
        commandProcessor.registerCommand(HelloCommand().engine)
        commandProcessor.registerCommand(StartCommand().engine)
        commandProcessor.registerCommand(HelpCommand().engine)
        commandProcessor.registerCommand(LockCommand().engine)
        commandProcessor.registerCommand(UnlockCommand().engine)
        commandProcessor.registerCommand(RestrictCommand().engine)
        commandProcessor.registerCommand(YTCommand().engine)
    }

    /**
     * On update: fire commands if it's a recognized command or is part of a ask-answer command, else manage in a different way
     */
    override fun onUpdateReceived(update: Update) {
        //check if it's a command and attempt to fire the response
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
            } else if (MultiCommandsHandler.instance.fireCommand(update.message, this)){ //check if the message is part of a ask-answer interaction
                //Nothing to do here, as the command is fired directly in the 'if'
            } else if (update.message.chat.isUserChat && update.hasMessage() && update.message.hasText()) { //manage normal commands
                val message = SendMessage()
                        .setChatId(update.message.chatId)
                        .setText("I'm a parrot\n ${update.message.text}")
                try {
                    execute<Message, SendMessage>(message)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }
            }
        }
    }
}