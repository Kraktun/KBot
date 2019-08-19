package com.kraktun.kbot.commands.core

import com.kraktun.kbot.jobs.JobInfo
import com.kraktun.kbot.utils.username
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.time.Instant

/**
 * Handles multi-input commands (commands that need more messages). ForceReply is not enough for me.
 */

const val TAG = "MULTICOMMANDS_HANDLER"

object MultiCommandsHandler {

    /*
    Map that contains a pair of user + chatId and the next command to execute for the user in that chat + data to pass to the command
     */
    @Volatile private var map = mutableMapOf<Triple<String, Int, Long>, MultiBaseCommand>()
    private const val maxCommandTime: Long = 60L // seconds. After this time has passed the user must resend the activation command.

    /**
     * Execute next command for pair user + chat.
     * False if no command is found.
     */
    fun fireCommand(message: Message, absSender: AbsSender): Boolean {
        var temp: MultiBaseCommand?
        synchronized(this) {
            temp = map[Triple(absSender.username(), message.from.id, message.chatId)]
        }
        if (temp != null) deleteCommand(absSender, message.from, message.chat)
        val executor = temp?.multiInterface
        runBlocking {
            GlobalScope.launch {
                executor?.executeAfter(absSender,
                    message.from,
                    message.chat,
                    message.text,
                    message,
                    temp?.data)
            }
        }
        return executor != null
    }

    /**
     * Insert new command in chat by user. Overwrite if already present.
     */
    fun insertCommand(absSender: AbsSender, user: Int, chat: Long, command: MultiCommandInterface, data: Any? = null) {
        // printlnK(TAG, "Received command ($user + $chat)")
        synchronized(this) {
            map[Triple(absSender.username(), user, chat)] = MultiBaseCommand(command, data)
        }
    }

    /**
     * Same as above, different signature
     */
    fun insertCommand(absSender: AbsSender, user: User, chat: Chat, command: MultiCommandInterface, data: Any? = null) {
        insertCommand(absSender, user.id, chat.id, command, data)
    }

    /**
     * Delete last command with pair user and chat.
     * A command is automatically deleted after execution.
     */
    fun deleteCommand(absSender: AbsSender, user: Int, chat: Long) {
        // printlnK(TAG, "Deleting command ($user + $chat)")
        synchronized(this) {
            map.remove(Triple(absSender.username(), user, chat))
        }
    }

    /**
     * Same as above, different signature
     */
    fun deleteCommand(absSender: AbsSender, user: User, chat: Chat) {
        deleteCommand(absSender, user.id, chat.id)
    }

    /**
     * Delete last command with pair userId and chatId.
     * Unsynchronized version.
     */
    private fun deleteUnsynch(botUsername: String, userId: Int, chatId: Long) {
        map.remove(Triple(botUsername, userId, chatId))
    }

    class CleanerJob : InterruptableJob {

        companion object {
            val jobInfo = JobInfo(
                name = "MULTICOMMANDCLEANER",
                interval = 15, // seconds
                trigger = "MULTICOMMANDCLEANER_TRIGGER",
                group = "jobs",
                delay = 10)
        }

        private val TAG = "MULTI_COMMAND_CLEANER"

        @Throws(JobExecutionException::class)
        override fun execute(context: JobExecutionContext) {
            val now = Instant.now()
            synchronized(MultiCommandsHandler) {
                map.filter {
                    it.value.time.plusSeconds(maxCommandTime).isBefore(now)
                }.forEach {
                    deleteUnsynch(it.key.first, it.key.second, it.key.third)
                    // printlnK(TAG, "Cleaned command ${it.key}")
                }
            }
        }

        override fun interrupt() {
            // interrupt
        }
    }
}