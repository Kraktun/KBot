package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandsHandler
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
class MultiExampleCommand : CommandInterface, MultiCommandInterface {

    val engine = BaseCommand(
        command = "multi",
        description = "Multi",
        targets = listOf(Target.USER),
        privacy = Status.DEV,
        argsNum = 0,
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        answer.text = "First part"
        MultiCommandsHandler.instance.insertCommand(userId = user.id, chatId = chat.id, command = this)
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        answer.text = "Second part"
        MultiCommandsHandler.instance.insertCommand(userId = user.id, chatId = chat.id, command = ThirdStep(), data = arguments)
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    class ThirdStep : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            val answer = SendMessage()
            answer.chatId = chat.id.toString()
            answer.text = "Third part, data is $data"
            try {
                absSender.execute(answer)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }

    }

}