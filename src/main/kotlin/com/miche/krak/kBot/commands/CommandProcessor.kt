package com.miche.krak.kBot.commands

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Process commands from bot, locating the correct command to call
 */
class CommandProcessor {

    companion object {
        val instance = CommandProcessor()
    }

    var map = mutableMapOf<String, KCommand>()

    /**
     * Register command
     */
    fun registerCommand(kCommand : KCommand) {
        if (map.containsKey(kCommand.command))
            throw CommandAlreadyRegisteredException()
        map[kCommand.command] = kCommand
    }

    /**
     * Execute command if it was registered.
     * Return false if no command was found
     */
    fun fireCommand(update : Update, absSender: AbsSender) : Boolean{
        val commandInput = update.message.text.plus(" ").substringBefore(" ").substring(1) //take first word (Add space at the end), remove appended '/'
        return map[commandInput]?.execute(absSender, update.message.from, update.message.chat, update.message.text.substringAfter(" ").split(" ")) != null
    }

    class CommandAlreadyRegisteredException : Exception() {

        override val message: String?
            get() = "A command with the same activation string is already present"
    }
}