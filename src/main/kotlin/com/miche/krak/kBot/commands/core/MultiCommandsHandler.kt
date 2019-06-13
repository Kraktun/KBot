package com.miche.krak.kBot.commands.core

import com.miche.krak.kBot.utils.printlnK
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import java.time.Instant

/**
 * Handles multi-input commands (commands that need more messages)
 */

const val TAG = "MULTICOMMANDS_HANDLER"

object MultiCommandsHandler {

    /*
    Map that contains a pair of user + chatId and the next command to execute for the user in that chat + data to pass to the command
     */
    @Volatile private var map = mutableMapOf<Pair<Int, Long>, MultiBaseCommand>()
    private const val maxCommandTime = 30L //seconds

    /**
     * Execute next command for pair user + chat.
     * False if no command is found.
     */
    fun fireCommand(message : Message, absSender: AbsSender) : Boolean {
        var temp : MultiBaseCommand?
        synchronized(this) {
            temp = map[Pair(message.from.id, message.chatId)]
        }
        if (temp != null) deleteCommand(message.from.id, message.chatId)
        return temp?.multiInterface?.executeAfter(absSender,
            message.from,
            message.chat,
            message.text,
            message,
            temp?.data) != null
    }

    /**
     * Insert new command in chatId by userId. Overwrite if already present.
     */
    fun insertCommand(userId : Int, chatId : Long, command : MultiCommandInterface, data: Any? = null) {
        printlnK(TAG, "Received command ($userId + $chatId)")
        synchronized(this) {
            map[Pair(userId, chatId)] = MultiBaseCommand(command, data)
        }
    }

    /**
     * Delete last command with pair userId and chatId.
     * A command is automatically deleted after execution.
     */
    fun deleteCommand(userId : Int, chatId : Long) {
        printlnK(TAG, "Deleting command ($userId + $chatId)")
        synchronized(this) {
            map.remove(Pair(userId, chatId))
        }
    }

    /**
     * Delete last command with pair userId and chatId.
     * Unsynchronized version.
     */
    private fun deleteUnsynch(userId : Int, chatId : Long) {
        map.remove(Pair(userId, chatId))
    }


    class CleanerJob : InterruptableJob {

        companion object {
            const val name : String = "MULTICOMMANDCLEANER"
            const val interval : Int = 10 //seconds
            const val trigger : String = "MULTICOMMANDCLEANER_TRIGGER"
            const val group : String = "cleaners"
        }

        private val TAG = "MULTI_COMMAND_CLEANER"

        @Throws(JobExecutionException::class)
        override fun execute(context: JobExecutionContext) {
            val now = Instant.now()
            synchronized(MultiCommandsHandler) {
                map.filter {
                    it.value.time.plusSeconds(maxCommandTime).isBefore(now)
                }.forEach {
                    deleteUnsynch(it.key.first, it.key.second)
                    printlnK(TAG, "Cleaned command ${it.key}")
                }
            }
        }

        override fun interrupt() {
            //interrupt
        }
    }
}