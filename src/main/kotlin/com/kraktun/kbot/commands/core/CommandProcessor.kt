package com.kraktun.kbot.commands.core

import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.username
import com.kraktun.kutils.other.readInLock
import com.kraktun.kutils.other.writeInLock
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * Process commands from bot, locating the correct command to call
 */
object CommandProcessor {

    @Volatile private var map = mutableMapOf<Pair<String, String>, BaseCommand>()
    private val lock = ReentrantReadWriteLock()

    /**
     * Register command. The string for the command must be unique.
     */
    fun registerCommand(botUsername: String, kCommand: BaseCommand) {
        lock.writeInLock {
            if (map.containsKey(Pair(botUsername, kCommand.command)))
                throw CommandAlreadyRegisteredException()
            map[Pair(botUsername, kCommand.command)] = kCommand
        }
    }

    /**
     * Return all registered commands.
     */
    fun getRegisteredCommands(botUsername: String): List<BaseCommand> {
        lock.readInLock {
            return map.filterKeys { it.first == botUsername }.values.toList()
        }
    }

    /**
     * Return all registered commands for pair user, chat.
     */
    fun getRegisteredCommands(absSender: AbsSender, status: Status, c: Target): List<BaseCommand> {
        return getRegisteredCommands(absSender.username()).filter {
            val r = it.targets[c]
            r != null && r <= status
        }
    }

    /**
     * Execute command if it's found.
     * Return NOT_COMMAND if no command is found for parsed update.
     * Results are propagated to the calling class even if now it is useless
     */
    fun fireCommand(message: Message, absSender: AbsSender): FilterResult {
        lock.readInLock {
            val botName = absSender.username()
            val commandInput = message.text.plus(" ") // Add space at the end, for single-word commands
                .substringBefore(" ") // take first word
                .plus("@$botName") // fixes commands in groups, where command can be in the form command@botName
                .substringBefore("@$botName")
            return map[Pair(botName, commandInput)]?.fire(
                absSender,
                message
            ) ?: FilterResult.NOT_COMMAND // when key is not present, map[]? equals null, so return is NOT_COMMAND
        }
    }

    /**
     * Simple exception
     */
    class CommandAlreadyRegisteredException : Exception() {

        override val message: String?
            get() = "A command with the same activation string is already present"
    }
}
