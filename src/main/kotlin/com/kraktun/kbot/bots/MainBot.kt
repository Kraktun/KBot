package com.kraktun.kbot.bots

import com.kraktun.kbot.*
import com.kraktun.kbot.commands.common.*
import com.kraktun.kbot.commands.core.*
import com.kraktun.kbot.commands.core.callbacks.CallbackProcessor
import com.kraktun.kbot.commands.functions.YTCommand
import com.kraktun.kbot.commands.groups.*
import com.kraktun.kbot.utils.*
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * Main class: register the commands and process non-command updates
 */
class MainBot(options: DefaultBotOptions) : TelegramLongPollingBot(options) {

    /**
     * Return username of the bot
     */
    override fun getBotUsername(): String? {
        return TEST_NAME
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
        // CommandProcessor.registerCommand(botUsername!!, TestCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, HelloCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, StartCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, HelpCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, LockCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, UnlockCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, RestrictCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, YTCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, BanCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, KickCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, UnbanCommand().engine)
        // CommandProcessor.registerCommand(botUsername!!, MultiExampleCommand().engine)
        // CommandProcessor.registerCommand(botUsername!!, KeyboardExampleCommand().engine)
        // CommandProcessor.registerCommand(botUsername!!, TrackCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, AdminCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, FormattedHelpCommand().engine)
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
        // printlnK("MAIN", "MESSAGE IS: $message")
        when {
            // If it's a group
            // Remove new user if it's banned, otherwise welcome him
            (message.isGroupOrSuper() && message.newChatMembers.isNotEmpty()) -> {
                if (BaseCommand.filterBans(user, chat)) { // TODO check if it fails with multiple new members one of which is banned
                    val welcomeU = message.newChatMembers.map {
                        it.getFormattedName()
                    }.reduce { acc, sing ->
                        "$acc $sing,"
                    }
                    simpleMessage(this, "Welcome $welcomeU", chat)
                } else {
                    kickUser(this, user, chat)
                }
            }

            // Check if chat is locked or user is banned  and if so delete the message
            (!BaseCommand.filterLock(user, chat) || !BaseCommand.filterBans(user, chat)) -> {
                deleteMessage(this, message)
            }

            // Check if it's a ask-answer interaction
            // Nothing to do here, as the command is fired directly in the 'if'
            // Note that this goes after the check on locks and bans, as the commands in MultiCommandsHandler
            // do not implement a check on bans and locks
            MultiCommandsHandler.fireCommand(message, this) -> { }

            // Check if it's a command and attempt to fire the response
            // Nothing to do in the function here, as the command is fired directly in the 'if'.
            // This goes after multiCommandsHandler as you may need to use a command in a multiCommand interaction
            CommandProcessor.fireCommand(update, this) != FilterResult.NOT_COMMAND -> {}

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