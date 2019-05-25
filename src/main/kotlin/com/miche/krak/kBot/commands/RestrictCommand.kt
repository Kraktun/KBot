package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.utils.Status
import com.miche.krak.kBot.utils.Target
import com.miche.krak.kBot.utils.getQualifiedUser
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import kotlin.math.abs


/**
 * Restrict command
 */
class RestrictCommand : CommandInterface {

    val engine = BaseCommand(
        command = "restrict",
        description = "Restricts a member in a group using telegram apis",
        targets = listOf(Target.SUPERGROUP),
        privacy = Status.ADMIN,
        argsNum = 0,
        filterFun = { m : Message ->
            m.isReply
        },
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        answer.text = "Restricted user ${getQualifiedUser(message.replyToMessage.from)}"
        val restricted = RestrictChatMember()
        restricted.chatId = chat.id.toString()
        restricted.userId = message.replyToMessage.from.id
        restricted.canAddWebPagePreviews = false
        restricted.canSendMediaMessages = false
        restricted.canSendMessages = false
        restricted.canSendOtherMessages = false
        try {
            absSender.execute(restricted)
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            try {
                answer.text = "Error: ${e.message}"
                absSender.execute(answer)
            } catch (ee: TelegramApiException) {
                ee.printStackTrace()
            }
            e.printStackTrace()
        }
    }
}