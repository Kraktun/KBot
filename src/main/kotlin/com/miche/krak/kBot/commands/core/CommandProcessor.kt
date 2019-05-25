package com.miche.krak.kBot.commands.core

import com.miche.krak.kBot.bots.MainBot.Companion.botName
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Process commands from bot, locating the correct command to call
 */
class CommandProcessor {

    companion object {
        val instance by lazy { CommandProcessor() }
    }

    private var map = mutableMapOf<String, BaseCommand>()
    private val lock = ReentrantLock() //lock shouldn't be necessary as a companion object

    /**
     * Register command. The string for the command must be unique.
     */
    fun registerCommand(kCommand : BaseCommand) {
        lock.withLock {
            if (map.containsKey(kCommand.command))
                throw CommandAlreadyRegisteredException()
            map[kCommand.command] = kCommand
        }
    }

    /**
     * Return all registered commands.
     */
    fun getRegisteredCommands() : List<BaseCommand> {
        return map.values.toList()
    }

    /**
     * Execute command if it's found.
     * Return false if no command is found for parsed update.
     */
    fun fireCommand(update : Update, absSender: AbsSender) : Boolean{
        val commandInput = update.message.text.plus(" ") //Add space at the end, for single-word commands
            .substringBefore(" ") //take first word
            .substring(1) //remove pre-pended '/'
            .plus("@$botName") //fixes unrecognized commands in groups
            .substringBefore("@$botName")
        var kCommand : BaseCommand? = null
        lock.withLock {
            kCommand = map[commandInput]
        }
        return kCommand?.fire(absSender,
            update.message.from,
            update.message.chat,
            update.message.text.substringAfter(" ") //take args from second word (first is the command)
                .split(" "), //put each word in the list
            update.message
        ) == true //when key is not present, map[]? equals null
    }

    /**
     * Simple exception
     */
    class CommandAlreadyRegisteredException : Exception() {

        override val message: String?
            get() = "A command with the same activation string is already present"
    }
}