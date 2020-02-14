package com.kraktun.kbot.commands.core

import com.kraktun.kbot.jobs.JobInfo
import com.kraktun.kbot.utils.username
import com.kraktun.kutils.other.readInLock
import com.kraktun.kutils.other.writeInLock
import java.time.Instant
import java.util.concurrent.locks.ReentrantReadWriteLock
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

/**
 * Handles multi-input commands (commands that need more messages). ForceReply is not enough for me.
 * Channels are not supported.
 */

object MultiCommandsHandler {

    /*
    Map that contains a pair of user + chatId and the next command to execute for the user in that chat + data to pass to the command
     */
    @Volatile private var map = mutableMapOf<MultiCommandChat, MultiBaseCommand>()
    private val lock = ReentrantReadWriteLock()

    /**
     * Execute next command for pair user + chat.
     * False if no command is found or message is from a channel.
     */
    fun fireCommand(message: Message, absSender: AbsSender): Boolean {
        if (message.isChannelMessage) return false
        var temp: MultiBaseCommand? = null
        lock.readInLock {
            temp = map[MultiCommandChat(absSender.username(), message.from.id, message.chatId)]
        }
        if (temp != null) deleteCommand(absSender, message.from, message.chat)
        val executor = temp?.multiInterface
        runBlocking {
            GlobalScope.launch {
                executor?.executeAfter(absSender,
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
        lock.writeInLock {
            map[MultiCommandChat(absSender.username(), user, chat)] = MultiBaseCommand(command, data)
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
        lock.writeInLock {
            map.remove(MultiCommandChat(absSender.username(), user, chat))
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
        map.remove(MultiCommandChat(botUsername, userId, chatId))
    }

    class CleanerJob : InterruptableJob {

        companion object {
            val jobInfo = JobInfo(
                name = "MULTICOMMANDCLEANER",
                interval = 5, // seconds
                trigger = "MULTICOMMANDCLEANER_TRIGGER",
                group = "jobs",
                delay = 10)
        }

        @Throws(JobExecutionException::class)
        override fun execute(context: JobExecutionContext) {
            val now = Instant.now()
            lock.writeInLock {
                map.filter {
                    it.value.time.plusSeconds(it.value.TTL).isBefore(now)
                }.forEach {
                    deleteUnsynch(it.key.bot, it.key.user, it.key.chat)
                }
            }
        }

        override fun interrupt() { }
    }
}
