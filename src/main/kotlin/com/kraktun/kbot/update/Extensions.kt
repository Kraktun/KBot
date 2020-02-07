package com.kraktun.kbot.update

import com.kraktun.kbot.commands.callbacks.CallbackProcessor
import com.kraktun.kbot.commands.core.CommandProcessor
import com.kraktun.kbot.commands.core.FilterResult
import com.kraktun.kbot.commands.core.MultiCommandsHandler
import com.kraktun.kbot.utils.isGroupOrSuper
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Digest an update, i.e. find if it's a command or an event and execute custom methods accordingly
 */
fun AbsSender.digest(
    update: Update,
    // execute checks before starting digest
    onPreDigest: (update: Update) -> Boolean = {true}, // true to continue, false to exit digest
    // execute when new user enters group
    onNewUser: (update: Update) -> Unit = {},
    // execute when command fired (after the command has been processed)
    onBaseCommand: (update: Update) -> Unit = {},
    // execute when multi command fired (after the command has been processed)
    onMultiCommand: (update: Update) -> Unit = {},
    // execute when callback fired (after the callback has been processed)
    onCallbackMessage: (update: Update) -> Unit = {},
    // execute when there is no match
    onElse: (update: Update) -> Unit = {}
    ) {
    if(!onPreDigest.invoke(update))
        return
    if (update.hasCallbackQuery()) {
        CallbackProcessor.fireCallback(absSender = this, callback = update.callbackQuery, user = update.callbackQuery.from.id)
        onCallbackMessage.invoke(update)
        return
    }
    val message = if (update.channelPost != null) update.channelPost else update.message
    when {
        // If it's a group
        // Remove new user if it's banned, otherwise welcome him
        (message.isGroupOrSuper() && message.newChatMembers.isNotEmpty()) -> {
            onNewUser.invoke(update)
        }

        // Check if it's a ask-answer interaction
        // Nothing to do here, as the command is fired directly in the 'if'
        // Note that this goes after the check on locks and bans, as the commands in MultiCommandsHandler
        // do not implement a check on bans and locks
        MultiCommandsHandler.fireCommand(message, this) -> {
            onMultiCommand.invoke(update)
        }

        // Check if it's a command and attempt to fire the response
        // Nothing to do in the function here, as the command is fired directly in the 'if'.
        // This goes after multiCommandsHandler as you may need to use a command in a multiCommand interaction
        CommandProcessor.fireCommand(message, this) != FilterResult.NOT_COMMAND -> {
            onBaseCommand.invoke(update)
        }

        else -> {
            onElse.invoke(update)
        }
    }
}