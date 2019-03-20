package com.miche.krak.kBot.commands.core

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

interface CommandInterface {

    fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>)
}