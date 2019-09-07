package com.kraktun.kbot.commands.common

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.commands.core.CommandProcessor
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.simpleHTMLMessage
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

    override fun execute(absSender: AbsSender, message: Message) {
        var text = ""
        CommandProcessor.getRegisteredCommands(absSender, Status.ADMIN, Target.GROUP).filter {
            it.command.startsWith("/")
        }.forEach {
            text += "${it.command.substringAfter("/")} - ${it.description}\n"
        }
        simpleHTMLMessage(absSender, text, message.chat)
    }
}