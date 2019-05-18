package com.miche.krak.kBot.commands.core

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Handles multi-input commands (commands that need more messages)
 */
class MultiCommandsHandler private constructor() {

    companion object {
        val instance by lazy { MultiCommandsHandler() }
    }

    /*
    Map that contains a pair of user + chatId and the last command sent by the user in that chat
     */
    private val map : MutableMap<Pair<Int, Long>, Pair<MultiCommandInterface, Any?>> by lazy {
        mutableMapOf<Pair<Int, Long>, Pair<MultiCommandInterface, Any?>>()
    }
    private val lock = ReentrantLock()

    /**
     * Get last command sent in chatId by userId, null if not found
     */
    fun fireCommand(message : Message, absSender: AbsSender) : Boolean {
        lock.withLock {
            val temp = map[Pair(message.from.id, message.chatId)]
            return temp?.first?.executeAfter(absSender,
                message.from,
                message.chat,
                message.text,
                message,
                temp.second) != null
        }
    }

    /**
     * Insert new command in chatId by userId
     */
    fun insertCommand(userId : Int, chatId : Long, command : MultiCommandInterface, data: Any? = null) {
        lock.withLock {
            map[Pair(userId, chatId)] = Pair(command, data)
        }
    }

    /**
     * Last command by userId in chatId has been processed, so delete it.
     */
    fun deleteCommand(userId : Int, chatId : Long) {
        lock.withLock {
            map.remove(Pair(userId, chatId))
        }
    }
}