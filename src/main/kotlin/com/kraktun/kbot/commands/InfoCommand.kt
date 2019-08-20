package com.kraktun.kbot.commands

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.simpleMessage
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * Simple command
 */
class InfoCommand : CommandInterface { // Implement CommandInterface (execute method)

    val engine = BaseCommand(
        command = "/info",
        description = "Get basic info",
        targets = listOf(Pair(Target.USER, Status.ADMIN),
            Pair(Target.GROUP, Status.ADMIN),
            Pair(Target.SUPERGROUP, Status.ADMIN)),
        exe = this
    )

    override fun execute(absSender: AbsSender, message: Message) {
        simpleMessage(absSender = absSender, s = "The chat id is: ${message.chatId}", c = message.chat)
    }
}