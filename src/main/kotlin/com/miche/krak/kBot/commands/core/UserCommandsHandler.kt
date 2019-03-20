package com.miche.krak.kBot.commands.core

/**
 * Handles multi-input commands (commands that need more messages)
 */
class UserCommandsHandler private constructor() {

    companion object {
        val instance = UserCommandsHandler()
    }

    /*
    Map that contains a pair of user + chatId and the last command sent by the user in that chat
     */
    private val map : MutableMap<Pair<Int, Int>, String> by lazy {
        mutableMapOf<Pair<Int, Int>, String>()
    }

    /**
     * Get last command sent in chatId by userId, null if not found
     */
    fun getCommand(userId : Int, chatId : Int) : String? {
        return map[Pair(userId, chatId)]
    }

    /**
     * Insert new command in chatId by userId
     */
    fun insertCommand(userId : Int, chatId : Int, command : String) {
        map[Pair(userId, chatId)] = command
    }

    /**
     * Last command by userId in chatId has been processed, so delete it.
     */
    fun deleteCommand(userId : Int, chatId : Int) {
        map.remove(Pair(userId, chatId))
    }
}