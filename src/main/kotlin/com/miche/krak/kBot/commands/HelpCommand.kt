package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.CommandProcessor
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.chatMapper
import com.miche.krak.kBot.utils.getDBStatus
import com.miche.krak.kBot.utils.simpleHTMLMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * Help command.
 * List all commands registered.
 */
class HelpCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/help",
        description = "Show a list of commands",
        targets = listOf(Pair(Target.USER, Status.USER),
            Pair(Target.GROUP, Status.NOT_REGISTERED),
            Pair(Target.SUPERGROUP, Status.NOT_REGISTERED)),
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        var text = "<b>Here is a list of all the commands</b>:\n"
        CommandProcessor.getRegisteredCommands(getDBStatus(user, chat), chatMapper(chat)).forEach {
            text += "${it.command} : ${it.description}\n"
        }
        simpleHTMLMessage(absSender, text, chat)
    }
}