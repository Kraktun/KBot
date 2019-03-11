package com.miche.krak.kBot.commands

import com.miche.krak.kBot.utils.Privacy
import com.miche.krak.kBot.utils.Target
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage


/**
 * Simple hello command
 */
class HelloCommand : KCommand(command, desc) {

    companion object {
        private const val command : String = "hello"
        private const val desc : String = "Hi"
    }

    private val filter = FilterCommand(
            targets = listOf(Target.USER, Target.GROUP),
            privacy = Privacy.USER,
            argsNum = 0)


    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>) {
        if (!filter.filterFrom(user, chat) || !filter.filterFormat(arguments))
            return
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        answer.text = "Hello there"
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {

        }

    }
}