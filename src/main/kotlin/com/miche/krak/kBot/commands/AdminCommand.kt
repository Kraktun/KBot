package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.CommandProcessor
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.*
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * Admin command.
 * Send keyboard with commands available for admins
 */
class AdminCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/admin",
        description = "Send keyboard with commands available for admins",
        targets = listOf(Pair(Target.USER, Status.ADMIN)),
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        val commands = mutableListOf<String>()
        CommandProcessor.getRegisteredCommands(getDBStatus(user, chat), chatMapper(chat)).forEach {
            commands.add(it.command)
        }
        commands.sort()
        val keyboard = getSimpleListKeyboard(list = commands, buttonsInRow = 3)
        keyboard.oneTimeKeyboard = true
        sendKeyboard(absSender = absSender, c = chat, s = "Here are the commands", keyboard = keyboard)
    }
}