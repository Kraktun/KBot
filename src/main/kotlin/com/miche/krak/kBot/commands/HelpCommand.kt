package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.CommandProcessor
import com.miche.krak.kBot.utils.Status
import com.miche.krak.kBot.utils.Target
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message


/**
 * Help command
 */
class HelpCommand : CommandInterface {

    val engine = BaseCommand(
        command = "help",
        description = "Show a list of commands",
        targets = listOf(Target.USER, Target.GROUP, Target.SUPERGROUP),
        privacy = Status.USER,
        argsNum = 0,
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        var text = "<b>Here is a list of all the commands</b>:\n"
        CommandProcessor.getRegisteredCommands().map {
            text += "/${it.command} : ${it.description}\n"
        }
        answer.text = text
        answer.enableHtml(true)
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }
}