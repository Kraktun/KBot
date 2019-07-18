package com.miche.krak.kBot.bots

import com.miche.krak.kBot.*
import com.miche.krak.kBot.commands.*
import com.miche.krak.kBot.commands.core.*
import com.miche.krak.kBot.commands.core.callbacks.CallbackProcessor
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

    companion object {
        lateinit var instance: TelegramLongPollingBot
    }

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
        // CommandProcessor.registerCommand(TestCommand().engine)
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
        // CommandProcessor.registerCommand(MultiExampleCommand().engine)
        // CommandProcessor.registerCommand(KeyboardExampleCommand().engine)
        // CommandProcessor.registerCommand(TrackCommand().engine)
        CommandProcessor.registerCommand(AdminCommand().engine)
        CommandProcessor.registerCommand(FormattedHelpCommand().engine)
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
        // printlnK("MAIN", "MESSAGE IS: $message")
        when {
            // If it's a group
            // Remove new user if it's banned, otherwise welcome him
            (message.isGroupOrSuper() && message.newChatMembers.isNotEmpty()) -> {
                if (BaseCommand.filterBans(user, chat)) { // TODO check if it fails with multiple new members one of which is banned
                    val welcomeU = message.newChatMembers.map {
                        getQualifiedUser(it)
                    }.reduce { acc, sing ->
                        "$acc $sing,"
                    }
                    simpleMessage(instance, "Welcome $welcomeU", chat)
                } else {
                    kickUser(instance, user, chat)
                }
            }

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