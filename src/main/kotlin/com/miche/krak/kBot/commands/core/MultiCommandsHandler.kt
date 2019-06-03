package com.miche.krak.kBot.commands.core

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Handles multi-input commands (commands that need more messages)
 */
object MultiCommandsHandler{

    /*
    Map that contains a pair of user + chatId and the next command to execute for the user in that chat + data to pass to the command
     */
    @Volatile private var map = mutableMapOf<Pair<Int, Long>, Pair<MultiCommandInterface, Any?>>()

    /**
     * Execute next command for pair user + chat.
     * False if no command is found.
     */
    fun fireCommand(message : Message, absSender: AbsSender) : Boolean {
        val temp = map[Pair(message.from.id, message.chatId)]
        deleteCommand(message.from.id, message.chatId)
        return temp?.first?.executeAfter(absSender,
            message.from,
            message.chat,
            message.text,
            message,
            temp.second) != null
    }

    /**
     * Insert new command in chatId by userId. Overwrite if already present.
     */
    fun insertCommand(userId : Int, chatId : Long, command : MultiCommandInterface, data: Any? = null) {
        map[Pair(userId, chatId)] = Pair(command, data)
    }

    /**
     * Delete last command with pair userId and chatId.
     * A command is automatically deleted after execution.
     */
    fun deleteCommand(userId : Int, chatId : Long) {
        map.remove(Pair(userId, chatId))
    }
}