package com.kraktun.kbot.commands.core

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

/*
Multiple replies can be stacked, creating a class extending this interface each time
See MultiExampleCommand for reference.
 */
interface MultiCommandInterface {

    /*
    The original message is passed if further information is needed (e.g. reply)
     */
    suspend fun executeAfter(absSender: AbsSender, message: Message, data: Any?)
}
