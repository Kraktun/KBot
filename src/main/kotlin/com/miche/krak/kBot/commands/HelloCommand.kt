package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.getQualifiedUser
import com.miche.krak.kBot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
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

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        simpleMessage(absSender = absSender, s = "Hello there, ${getQualifiedUser(user)}", c = chat)
    }
}