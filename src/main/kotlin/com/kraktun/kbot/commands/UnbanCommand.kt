package com.kraktun.kbot.commands

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.ChatOptions
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.arguments
import com.kraktun.kbot.utils.logK
import com.kraktun.kbot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.methods.groupadministration.UnbanChatMember
import org.telegram.telegrambots.meta.api.objects.Message
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
                // check if first parameter is an int
                it.text.substringAfter(" ").plus(" ").substringBefore(" ").toInt()
                true
            } catch (e: Exception) { false }
        },
        chatOptions = mutableListOf(ChatOptions.BOT_IS_ADMIN),
        onError = { absSender, message, _ ->
            simpleMessage(absSender, "Invalid data", message.chat)
        },
        exe = this
    )

    override fun execute(absSender: AbsSender, message: Message) {
        // DatabaseManager.addGroupUser(groupId = chat.id, userId = message.replyToMessage.from.id, statusK = Status.USER)
        val arguments = message.arguments()
        val method = UnbanChatMember()
            .setChatId(message.chatId)
            .setUserId(arguments[0].toInt())
        try {
            absSender.execute(method)
            simpleMessage(absSender = absSender, s = "Unbanned user ${arguments[0].toInt()}", c = message.chat)
        } catch (e: TelegramApiException) {
            logK("UNBANCOMMAND", e)
            e.printStackTrace()
            simpleMessage(absSender = absSender, s = "Error unbanning user", c = message.chat)
        }
    }
}