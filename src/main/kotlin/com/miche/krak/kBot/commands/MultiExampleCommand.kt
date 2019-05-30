package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandsHandler
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message


/**
 * Example for a multiple ask-answer command.
 */
class MultiExampleCommand : CommandInterface, MultiCommandInterface {

    val engine = BaseCommand(
        command = "multi",
        description = "Multi",
        targets = listOf(Pair(Target.USER, Status.DEV)),
        argsNum = 0,
        exe = this
    )

    /**
     * Executed when command is first sent.
     */
    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        answer.text = "First part"
        MultiCommandsHandler.insertCommand(userId = user.id, chatId = chat.id, command = this)
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    /**
     * Executed when first reply is received.
     * @arguments is the reply to what is sent above.
     * @data is usually empty here
     */
    override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        answer.text = "Second part"
        MultiCommandsHandler.insertCommand(userId = user.id, chatId = chat.id, command = ThirdStep(), data = arguments)
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    /**
     * Executed after the second reply.
     * @arguments is the new reply.
     * @data is the old reply.
     */
    class ThirdStep : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            MultiCommandsHandler.deleteCommand(userId = user.id, chatId = chat.id)
            val answer = SendMessage()
            answer.chatId = chat.id.toString()
            answer.text = "Third part, data is $data, \nnew data is $arguments"
            try {
                absSender.execute(answer)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }

    }

}