package com.miche.krak.kBot.bots

import com.miche.krak.kBot.*
import com.miche.krak.kBot.commands.*
import com.miche.krak.kBot.commands.core.CommandProcessor
import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.MultiCommandsHandler
import com.miche.krak.kBot.utils.*
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * Main class: register the commands and process non-command updates
 */
class MainBot(options: DefaultBotOptions) : TelegramLongPollingBot(options) {

    /*
     * Same as getBotUsername(), must be explicit so that the bot can process commands in groups in the form /hello@botName
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
        //CommandProcessor.registerCommand(TestCommand().engine)
        CommandProcessor.registerCommand(HelloCommand().engine)
        CommandProcessor.registerCommand(StartCommand().engine)
        CommandProcessor.registerCommand(HelpCommand().engine)
        CommandProcessor.registerCommand(LockCommand().engine)
        CommandProcessor.registerCommand(UnlockCommand().engine)
        CommandProcessor.registerCommand(RestrictCommand().engine)
        CommandProcessor.registerCommand(YTCommand().engine)
        CommandProcessor.registerCommand(BanCommand().engine)
        CommandProcessor.registerCommand(KickCommand().engine)
        CommandProcessor.registerCommand(UnbanCommand().engine)
    }

    /**
     * On update: fire commands if it's a recognized command or is part of a ask-answer command, else manage in a different way
     */
    override fun onUpdateReceived(update: Update) {
        val message = update.message
        val chat = message.chat
        val user = message.from
        when {
            //If it's a group
            //Remove new user if it's banned, otherwise welcome him
            ((message.isGroupMessage || message.isSuperGroupMessage) && message.newChatMembers.isNotEmpty()) -> {
                if (BaseCommand.filterBans(user, chat)) {
                    val welcomeU = message.newChatMembers.map {
                        getQualifiedUser(it)
                    }.reduce { acc, sing ->
                        "$acc $sing,"
                    }
                    simpleMessage(this, "Welcome $welcomeU", chat)
                } else {
                    kickUser(this, user, chat)
                }
            }

            //Check if it's a command and attempt to fire the response
            //Nothing to do in the function here, as the command is fired directly in the 'if'
            CommandProcessor.fireCommand(update, this) -> {}

            //Check if chat is locked or user is banned  and if so delete the message
            (!BaseCommand.filterLock(user, chat) || !BaseCommand.filterBans(user, chat)) -> {
                deleteMessage(this, message)
            }

            //Check if it's a ask-answer interaction
            //Nothing to do here, as the command is fired directly in the 'if'
            //Note that this goes after the check on locks and bans, as the commands in MultiCommandsHandler
            // do not implement a check on bans and locks
            MultiCommandsHandler.fireCommand(message, this) -> { }

            //manage normal messages
            (chat.isUserChat && update.hasMessage() && message.hasText()) -> {
                val reply = SendMessage()
                    .setChatId(chat.id)
                    .setText("I'm a parrot\n ${message.text}")
                try {
                    execute(reply)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }
            }
        }
    }
}