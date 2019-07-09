package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.ChatOptions
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.getQualifiedUser
import com.miche.krak.kBot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * Restrict command.
 * Use telegram API to restrict a user in a supergroup.
 */
class RestrictCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/restrict",
        description = "Restricts a member in a group using telegram apis",
        targets = listOf(Pair(Target.SUPERGROUP, Status.ADMIN)),
        filterFun = { m: Message ->
            m.isReply
        },
        chatOptions = mutableListOf(ChatOptions.BOT_IS_ADMIN),
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        val restricted = RestrictChatMember()
        restricted.chatId = chat.id.toString()
        restricted.userId = message.replyToMessage.from.id
        restricted.canAddWebPagePreviews = false
        restricted.canSendMediaMessages = false
        restricted.canSendMessages = false
        restricted.canSendOtherMessages = false
        try {
            absSender.execute(restricted)
            simpleMessage(absSender, "Restricted user ${getQualifiedUser(message.replyToMessage.from)}", chat)
        } catch (e: TelegramApiException) {
            simpleMessage(absSender, "Error: ${e.message}", chat)
            e.printStackTrace()
        }
    }
}