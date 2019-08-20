package com.kraktun.kbot.bots

import com.kraktun.kbot.*
import com.kraktun.kbot.commands.*
import com.kraktun.kbot.commands.core.*
import com.kraktun.kbot.commands.core.callbacks.CallbackProcessor
import com.kraktun.kbot.utils.*
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * Main class: register the commands and process non-command updates
 */
class PingBot(options: DefaultBotOptions) : TelegramLongPollingBot(options) {

    companion object {
        lateinit var instance: TelegramLongPollingBot
    }

    /**
     * Return username of the bot
     */
    override fun getBotUsername(): String? {
        return PING_BOT_NAME
    }

    /**
     * Return token of the bot
     */
    override fun getBotToken(): String? {
        return PING_BOT_TOKEN
    }

    /**
     * Register Commands
     */
    init {
        // CommandProcessor.registerCommand(botUsername!!, TestCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, HelloCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, StartCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, HelpCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, FormattedHelpCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, StartPingCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, StopPingCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, InfoCommand().engine)
        instance = this
    }

    /**
     * On update: fire commands if it's a recognized command or is part of a ask-answer command, else manage in a different way
     */
    override fun onUpdateReceived(update: Update) {
        // filter callbacks first, as they usually have a null message
        if (update.hasCallbackQuery()) {
            CallbackProcessor.fireCallback(absSender = this, callback = update.callbackQuery, user = update.callbackQuery.from.id)
            return
        }
        val message = update.message
        val chat = message.chat
        val user = message.from
        when {
            // If it's a group
            // Remove new user if it's banned, otherwise welcome him
            (message.isGroupOrSuper() && message.newChatMembers.isNotEmpty()) -> { }

            // Check if chat is locked or user is banned  and if so delete the message
            (!BaseCommand.filterLock(user, chat) || !BaseCommand.filterBans(user, chat)) -> {
                deleteMessage(instance, message)
            }

            // Check if it's a ask-answer interaction
            // Nothing to do here, as the command is fired directly in the 'if'
            // Note that this goes after the check on locks and bans, as the commands in MultiCommandsHandler
            // do not implement a check on bans and locks
            MultiCommandsHandler.fireCommand(message, instance) -> { }

            // Check if it's a command and attempt to fire the response
            // Nothing to do in the function here, as the command is fired directly in the 'if'.
            // This goes after multiCommandsHandler as you may need to use a command in a multiCommand interaction
            CommandProcessor.fireCommand(update, instance) != FilterResult.NOT_COMMAND -> {}

            // manage normal messages
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