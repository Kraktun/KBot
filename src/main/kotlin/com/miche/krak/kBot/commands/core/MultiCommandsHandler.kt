package com.miche.krak.kBot.commands.core

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Handles multi-input commands (commands that need more messages)
 */
object MultiCommandsHandler{

    /*
    Map that contains a pair of user + chatId and the last command sent by the user in that chat
     */
    @Volatile private var map = mutableMapOf<Pair<Int, Long>, Pair<MultiCommandInterface, Any?>>()

    /**
     * Get last command sent in chatId by userId, null if not found
     */
    fun fireCommand(message : Message, absSender: AbsSender) : Boolean {
        val temp = map[Pair(message.from.id, message.chatId)]
            return temp?.first?.executeAfter(absSender,
                message.from,
                message.chat,
                message.text,
                message,
                temp.second) != null
    }

    /**
     * Insert new command in chatId by userId
     */
    fun insertCommand(userId : Int, chatId : Long, command : MultiCommandInterface, data: Any? = null) {
        map[Pair(userId, chatId)] = Pair(command, data)
    }

    /**
     * Last command by userId in chatId has been processed, so delete it.
     */
    fun deleteCommand(userId : Int, chatId : Long) {
        map.remove(Pair(userId, chatId))
    }
}