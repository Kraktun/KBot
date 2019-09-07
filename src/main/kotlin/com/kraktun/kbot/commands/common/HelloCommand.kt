package com.kraktun.kbot.commands.common

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.getFormattedName
import com.kraktun.kbot.utils.simpleMessage
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * Simple hello command
 */
class HelloCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/hello",
        description = "Hi",
        targets = listOf(Pair(Target.USER, Status.USER),
            Pair(Target.GROUP, Status.NOT_REGISTERED),
            Pair(Target.SUPERGROUP, Status.NOT_REGISTERED)),
        exe = this
    )

    override fun execute(absSender: AbsSender, message: Message) {
        simpleMessage(absSender = absSender, s = "Hello there, ${message.from.getFormattedName()}", c = message.chat)
    }
}