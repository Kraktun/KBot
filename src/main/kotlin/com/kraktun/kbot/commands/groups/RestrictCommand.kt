package com.kraktun.kbot.commands.groups

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.ChatOptions
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.getFormattedName
import com.kraktun.kbot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember
import org.telegram.telegrambots.meta.api.objects.ChatPermissions
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

    override fun execute(absSender: AbsSender, message: Message) {
        val restricted = RestrictChatMember()
        restricted.chatId = message.chatId.toString()
        restricted.userId = message.replyToMessage.from.id
        val permissions = ChatPermissions() // Default everything is false
        restricted.permissions = permissions
        try {
            absSender.execute(restricted)
            simpleMessage(absSender, "Restricted user ${message.replyToMessage.from.getFormattedName()}", message.chat)
        } catch (e: TelegramApiException) {
            simpleMessage(absSender, "Error: ${e.message}", message.chat)
            e.printStackTrace()
        }
    }
}