package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.utils.Status
import com.miche.krak.kBot.utils.Target
import com.miche.krak.kBot.utils.getQualifiedUser
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message


/**
 * Simple hello command
 */
class HelloCommand : CommandInterface {

    val engine = BaseCommand(
        command = "hello",
        description = "Hi",
        targets = listOf(Target.USER, Target.GROUP, Target.SUPERGROUP),
        privacy = Status.USER,
        argsNum = 0,
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        answer.text = "Hello there ${getQualifiedUser(user)}"
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }
}