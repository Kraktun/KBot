package com.kraktun.kbot.commands.examples

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
class SimpleExampleCommand : CommandInterface { // Implement CommandInterface (execute method)

    // create an instance of baseCommand. See core.BaseCommand for a complete description.
    val engine = BaseCommand(
        command = "/simple",
        description = "This is the description",
        targets = listOf(Pair(Target.USER, Status.USER),
            Pair(Target.GROUP, Status.NOT_REGISTERED),
            Pair(Target.SUPERGROUP, Status.NOT_REGISTERED)),
        exe = this
    )

    override fun execute(absSender: AbsSender, message: Message) {
        simpleMessage(absSender = absSender, s = "Hello there, this is a simple command", c = message.chat)
    }
}