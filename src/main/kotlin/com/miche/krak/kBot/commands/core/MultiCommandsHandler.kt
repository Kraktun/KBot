package com.miche.krak.kBot.commands.core

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
    private val map : MutableMap<Pair<Int, Long>, String> by lazy {
        mutableMapOf<Pair<Int, Long>, String>()
    }
    private val lock = ReentrantLock()

    /**
     * Get last command sent in chatId by userId, null if not found
     */
    fun getCommand(userId : Int, chatId : Long) : String? {
        lock.withLock {
            return map[Pair(userId, chatId)]
        }
    }

    /**
     * Insert new command in chatId by userId
     */
    fun insertCommand(userId : Int, chatId : Long, command : String) {
        lock.withLock {
            map[Pair(userId, chatId)] = command
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