package com.kraktun.kbot.bots

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandProcessor
import com.kraktun.kbot.update.digest
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

open class SimpleLongPollingBot(private val username: String, private val token: String, botOptions: DefaultBotOptions = DefaultBotOptions()) : TelegramLongPollingBot(botOptions) {

    private var preDigest : (Update) -> Boolean = { true }
    private var newUser : (Update) -> Unit = { }
    private var baseCommand : (Update) -> Unit = { }
    private var callbackMessage : (Update) -> Unit = { }
    private var multiCommand : (Update) -> Unit = { }
    private var onElse : (Update) -> Unit = { }
    private var isRegistered: Boolean = false

    override fun getBotToken(): String {
        return token
    }

    override fun getBotUsername(): String {
        return username
    }

    override fun onUpdateReceived(update: Update?) {
        if (!isRegistered) throw BotIsNotRegisteredException()
        if (update == null) return
        digest(update,
            onPreDigest = preDigest,
            onNewUser = newUser,
            onBaseCommand = baseCommand,
            onCallbackMessage = callbackMessage,
            onMultiCommand = multiCommand,
            onElse = onElse
        )
    }

    fun withCommand(command: BaseCommand) : SimpleLongPollingBot {
        CommandProcessor.registerCommand(username, command)
        return this
    }

    fun onPreDigest(action: (Update) -> Boolean): SimpleLongPollingBot {
        preDigest = action
        return this
    }

    fun onNewUser(action: (Update) -> Unit): SimpleLongPollingBot {
        newUser = action
        return this
    }

    fun onBaseCommand(action: (Update) -> Unit): SimpleLongPollingBot {
        baseCommand = action
        return this
    }

    fun onCallbackMessage(action: (Update) -> Unit): SimpleLongPollingBot {
        callbackMessage = action
        return this
    }

    fun onMultiCommand(action: (Update) -> Unit): SimpleLongPollingBot {
        multiCommand = action
        return this
    }

    fun onElse(action: (Update) -> Unit): SimpleLongPollingBot {
        onElse = action
        return this
    }

    fun register() {
        isRegistered = true
        BotsManager.registerBot(this)
    }
}