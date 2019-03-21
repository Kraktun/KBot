package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.utils.Status
import com.miche.krak.kBot.utils.Target
import com.miche.krak.kBot.utils.getQualifiedUser
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage


/**
 * Simple hello command
 */
class StartCommand : CommandInterface {

    val engine = BaseCommand(
        command = "start",
        description = "Hi",
        targets = listOf(Target.USER, Target.GROUP),
        privacy = Status.NOT_REGISTERED,
        argsNum = 0,
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>) {
        DatabaseManager.instance.insertUser(user = user, userStatus = Status.USER)
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        answer.text = "Welcome ${getQualifiedUser(user)} your id is: ${DatabaseManager.instance.getUser(user.id).id}"
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {

        }

    }
}