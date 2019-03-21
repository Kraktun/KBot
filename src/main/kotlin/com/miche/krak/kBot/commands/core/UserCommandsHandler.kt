package com.miche.krak.kBot.commands.core

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Handles multi-input commands (commands that need more messages)
 */
class UserCommandsHandler private constructor() {

    companion object {
        val instance by lazy { UserCommandsHandler() }
    }

    /*
    Map that contains a pair of user + chatId and the last command sent by the user in that chat
     */
    private val map : MutableMap<Pair<Int, Int>, String> by lazy {
        mutableMapOf<Pair<Int, Int>, String>()
    }
    private val lock = ReentrantLock()

    /**
     * Get last command sent in chatId by userId, null if not found
     */
    fun getCommand(userId : Int, chatId : Int) : String? {
        lock.withLock {
            return map[Pair(userId, chatId)]
        }
    }

    /**
     * Insert new command in chatId by userId
     */
    fun insertCommand(userId : Int, chatId : Int, command : String) {
        lock.withLock {
            map[Pair(userId, chatId)] = command
        }
    }

    /**
     * Last command by userId in chatId has been processed, so delete it.
     */
    fun deleteCommand(userId : Int, chatId : Int) {
        lock.withLock {
            map.remove(Pair(userId, chatId))
        }
    }
}