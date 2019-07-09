package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.ChatOptions
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.FilterResult
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.getQualifiedUser
import com.miche.krak.kBot.utils.kickUser
import com.miche.krak.kBot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Kicks the user from a group without banning him. Available only in supergroups.
 */
class KickCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/kick",
        description = "Kick the user from a group",
        targets = listOf(
            Pair(Target.GROUP, Status.ADMIN),
            Pair(Target.SUPERGROUP, Status.ADMIN)),
        filterFun = { m: Message ->
            m.isReply
        },
        chatOptions = mutableListOf(ChatOptions.BOT_IS_ADMIN, ChatOptions.OPTION_ALL_USER_ADMIN_DISABLED),
        onError = { absSender, _, m, result ->
            if (result == FilterResult.INVALID_STATUS)
                simpleMessage(absSender = absSender, s = "Yeah, as if you could...", c = m.chat)
        },
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        if (chat.isGroupChat && DatabaseManager.getGroupUserStatus(chat.id, message.replyToMessage.from.id) == Status.ADMIN) {
            simpleMessage(absSender, "Admins can be removed only by the creator", chat)
            return
        }
        // DatabaseManager.addGroupUser(groupId = chat.id, userId = message.replyToMessage.from.id, statusK = Status.BANNED)
        kickUser(absSender = absSender, u = message.replyToMessage.from, c = chat)
        simpleMessage(absSender = absSender, s = "Kicked user ${getQualifiedUser(message.replyToMessage.from)}", c = chat)
    }
}