package com.kraktun.kbot.commands.core

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

interface CommandInterface {

    /*
    The original message is passed if further information is needed (e.g. reply)
     */
    fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message)
}