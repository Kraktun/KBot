package com.kraktun.kbot.bots

import com.kraktun.kbot.*
import com.kraktun.kbot.bots.ping.PONG
import com.kraktun.kbot.bots.ping.PingController
import com.kraktun.kbot.bots.ping.PingJob
import com.kraktun.kbot.bots.ping.PingListener
import com.kraktun.kbot.commands.common.FormattedHelpCommand
import com.kraktun.kbot.commands.common.HelpCommand
import com.kraktun.kbot.commands.common.StartCommand
import com.kraktun.kbot.commands.core.*
import com.kraktun.kbot.commands.core.callbacks.CallbackProcessor
import com.kraktun.kbot.commands.misc.StartPingCommand
import com.kraktun.kbot.commands.misc.StopPingCommand
import com.kraktun.kbot.jobs.JobExecutor
import com.kraktun.kbot.utils.*
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

/**
 * How to use this:
 * Register PingBot and PongBot in two different jar.
 * Add both bots to a channel as admins.
 * Use /startping from a user chat with PingBot.
 * Sit down and enjoy.
 */
class PingBot(options: DefaultBotOptions) : TelegramLongPollingBot(options), PingListener {

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
        CommandProcessor.registerCommand(botUsername!!, StartCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, HelpCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, FormattedHelpCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, StartPingCommand().engine)
        CommandProcessor.registerCommand(botUsername!!, StopPingCommand().engine)
        PingController.registerListener(this)
        simpleMessage(this, "Initiated boot sequence. Ping service is disabled.", PING_BOT_ALERT)
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
        val message = if (update.channelPost != null) update.channelPost else update.message
        val chat = message.chat
        val user = message.from
        when {
            // Check if chat is locked or user is banned  and if so delete the message
            user != null && (!BaseCommand.filterLock(user, chat) || !BaseCommand.filterBans(user, chat)) -> {
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
            CommandProcessor.fireCommand(message, this) != FilterResult.NOT_COMMAND -> { }

            (message.hasText() && chat.id == PING_PONG_CHAT && message.text.equals(PONG, ignoreCase = true)) -> {
                PingController.registerPong()
            }
        }
    }

    override fun onPongTimeExceeded() {
        simpleMessage(this, "Ping time exceeded. Disabled job.", PING_BOT_ALERT)
        JobExecutor.removeJob(PingJob.jobInfo)
    }
}