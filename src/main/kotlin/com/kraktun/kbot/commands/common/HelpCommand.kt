package com.kraktun.kbot.commands.common

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.commands.core.CommandProcessor
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.getDBStatus
import com.kraktun.kbot.utils.simpleHTMLMessage
import com.kraktun.kbot.utils.toEnum
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

    override fun execute(absSender: AbsSender, message: Message) {
        var text = "<b>Here is a list of all the commands</b>:\n"
        CommandProcessor.getRegisteredCommands(absSender, getDBStatus(message.from, message.chat), message.chat.toEnum()).forEach {
            text += "${it.command} : ${it.description}\n"
        }
        simpleHTMLMessage(absSender, text, message.chat)
    }
}