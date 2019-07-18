package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.CommandProcessor
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.simpleHTMLMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * Formatted help command.
 * List all commands registered for groups.
 */
class FormattedHelpCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/fhelp",
        description = "List all commands registered for groups in format parsable by BotFather",
        targets = listOf(Pair(Target.USER, Status.CREATOR)),
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        var text = ""
        CommandProcessor.getRegisteredCommands(Status.ADMIN, Target.GROUP).filter {
            it.command.startsWith("/")
        }.forEach {
            text += "${it.command.substringAfter("/")} - ${it.description}\n"
        }
        simpleHTMLMessage(absSender, text, chat)
    }
}