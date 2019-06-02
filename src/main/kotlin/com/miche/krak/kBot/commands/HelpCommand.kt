package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.CommandProcessor
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.chatMapper
import com.miche.krak.kBot.utils.getDBStatus
import com.miche.krak.kBot.utils.safeEmpty
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message


/**
 * Help command.
 * List all commands registered.
 */
class HelpCommand : CommandInterface {

    val engine = BaseCommand(
        command = "help",
        description = "Show a list of commands",
        targets = listOf(Pair(Target.USER, Status.USER),
            Pair(Target.GROUP, Status.NOT_REGISTERED),
            Pair(Target.SUPERGROUP, Status.NOT_REGISTERED)),
        argsNum = 0,
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        var text = "<b>Here is a list of all the commands</b>:\n"
        CommandProcessor.getRegisteredCommands().filter {
            it.targets.filter { m ->
                m.first == chatMapper(chat)
            }.safeEmpty ({
                this[0].second <= getDBStatus(user, chat) //[0] as a command can have only one single pair with a unique Target
        }, false) as Boolean}.map {
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