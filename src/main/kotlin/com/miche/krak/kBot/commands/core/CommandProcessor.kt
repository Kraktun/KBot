package com.miche.krak.kBot.commands.core

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Process commands from bot, locating the correct command to call
 */
class CommandProcessor {

    companion object {
        val instance by lazy { CommandProcessor() }
    }

    var map = mutableMapOf<String, BaseCommand>()

    /**
     * Register command
     */
    fun registerCommand(kCommand : BaseCommand) {
        if (map.containsKey(kCommand.command))
            throw CommandAlreadyRegisteredException()
        map[kCommand.command] = kCommand
    }

    /**
     * Execute command if it was registered.
     * Return false if no command was found
     */
    fun fireCommand(update : Update, absSender: AbsSender) : Boolean{
        val commandInput = update.message.text.plus(" ") //Add space at the end, for single-word commands
            .substringBefore(" ") //take first word
            .substring(1) //remove pre-pended '/'
        return map[commandInput]?.fire(absSender,
            update.message.from,
            update.message.chat,
            update.message.text.substringAfter(" ") //take args from second word (first is the command)
                .split(" ") //put each word in the list
        )!= null //when key is not present, map[]? equals null
    }

    /**
     * Simple exception
     */
    class CommandAlreadyRegisteredException : Exception() {

        override val message: String?
            get() = "A command with the same activation string is already present"
    }
}