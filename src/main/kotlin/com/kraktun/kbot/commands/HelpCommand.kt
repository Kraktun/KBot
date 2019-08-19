package com.kraktun.kbot.commands

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.commands.core.CommandProcessor
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.chatMapper
import com.kraktun.kbot.utils.getDBStatus
import com.kraktun.kbot.utils.simpleHTMLMessage
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
        CommandProcessor.getRegisteredCommands(absSender, getDBStatus(user, chat), chatMapper(chat)).forEach {
            text += "${it.command} : ${it.description}\n"
        }
        simpleHTMLMessage(absSender, text, chat)
    }
}