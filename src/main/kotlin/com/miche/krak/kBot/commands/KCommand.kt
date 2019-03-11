package com.miche.krak.kBot.commands

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Represents a command
 */
abstract class KCommand(val command : String, val description : String) {

    abstract fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>)
}

