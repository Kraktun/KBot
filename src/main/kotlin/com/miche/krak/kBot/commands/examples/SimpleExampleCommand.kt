package com.miche.krak.kBot.commands.examples

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
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

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        simpleMessage(absSender = absSender, s = "Hello there, this is a simple command", c = chat)
    }
}