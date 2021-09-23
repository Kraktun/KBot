package com.kraktun.kbot.commands.core

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

// Use SAM conversion
fun interface CommandInterface {

    /*
    The original message is passed if further information is needed (e.g. reply)
     */
    suspend fun execute(absSender: AbsSender, message: Message)
}
