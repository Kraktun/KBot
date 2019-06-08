package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.logK
import com.miche.krak.kBot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.methods.groupadministration.UnbanChatMember
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.lang.Exception

/**
 * Lifts ban from a user
 */
class UnbanCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/unban",
        description = "Lifts ban from a user. Usage: /unban + [ID]",
        targets = listOf(
            Pair(Target.SUPERGROUP, Status.ADMIN)),
        argsNum = 1,
        filterFun = {
            try {
                //check if first parameter is an int
                it.text.substringAfter(" ").plus(" ").substringBefore(" ").toInt()
                true
            } catch (e : Exception) {false}
        },
        onError = { absSender, _, message ->
            simpleMessage(absSender, "Invalid data", message.chat)
        },
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        //DatabaseManager.addGroupUser(groupId = chat.id, userId = message.replyToMessage.from.id, statusK = Status.USER)
        val method = UnbanChatMember()
            .setChatId(chat.id)
            .setUserId(arguments[0].toInt())
        try {
            absSender.execute(method)
            simpleMessage(absSender = absSender, s = "Unbanned user ${arguments[0].toInt()}", c = chat)
        } catch (e : TelegramApiException) {
            logK("UNBANCOMMAND", e)
            e.printStackTrace()
            simpleMessage(absSender = absSender, s = "Error unbanning user", c = chat)
        }
    }
}