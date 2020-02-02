package com.kraktun.kbot.commands.core

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

interface CommandInterface {

    /*
    The original message is passed if further information is needed (e.g. reply)
     */
    fun execute(absSender: AbsSender, message: Message)
}