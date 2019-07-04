package com.miche.krak.kBot.commands.core

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.generics.LongPollingBot

/**
 * Process commands from bot, locating the correct command to call
 */
object CommandProcessor {

    @Volatile private var map = mutableMapOf<String, BaseCommand>()

    /**
     * Register command. The string for the command must be unique.
     */
    fun registerCommand(kCommand: BaseCommand) {
        if (map.containsKey(kCommand.command))
            throw CommandAlreadyRegisteredException()
        map[kCommand.command] = kCommand
    }

    /**
     * Return all registered commands.
     */
    fun getRegisteredCommands(): List<BaseCommand> {
        return map.values.toList()
    }

    /**
     * Execute command if it's found.
     * Return NOT_COMMAND if no command is found for parsed update.
     * Results are propagated to the calling class even if now it is useless
     */
    fun fireCommand(update: Update, absSender: AbsSender): FilterResult {
        val botName = (absSender as LongPollingBot).botUsername
        val commandInput = update.message.text.plus(" ") // Add space at the end, for single-word commands
            .substringBefore(" ") // take first word
            .plus("@$botName") // fixes commands in groups, where command can be in the form command@botName
            .substringBefore("@$botName")
        return map[commandInput]?.fire(absSender,
            update.message.from,
            update.message.chat,
            update.message.text.substringAfter(" ") // take args from second word (first is the command)
                .split(" "), // put each word in the list
            update.message
        ) ?: FilterResult.NOT_COMMAND // when key is not present, map[]? equals null, so return is NOT_COMMAND
    }

    /**
     * Simple exception
     */
    class CommandAlreadyRegisteredException : Exception() {

        override val message: String?
            get() = "A command with the same activation string is already present"
    }
}