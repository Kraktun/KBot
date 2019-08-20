package com.kraktun.kbot.commands

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.ChatOptions
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.commands.core.FilterResult
import com.kraktun.kbot.database.DatabaseManager
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.getFormattedName
import com.kraktun.kbot.utils.kickUser
import com.kraktun.kbot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.objects.Message
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
        chatOptions = mutableListOf(ChatOptions.BOT_IS_ADMIN),
        onError = { absSender, m, result ->
            if (result == FilterResult.INVALID_STATUS)
                simpleMessage(absSender = absSender, s = "Yeah, as if you could...", c = m.chat)
        },
        exe = this
    )

    override fun execute(absSender: AbsSender, message: Message) {
        if (message.chat.isGroupChat && DatabaseManager.getGroupUserStatus(message.chat.id, message.replyToMessage.from.id) == Status.ADMIN) {
            simpleMessage(absSender, "Admins can be removed only by the creator", message.chat)
            return
        }
        // DatabaseManager.addGroupUser(groupId = chat.id, userId = message.replyToMessage.from.id, statusK = Status.BANNED)
        kickUser(absSender = absSender, u = message.replyToMessage.from, c = message.chat)
        simpleMessage(absSender = absSender, s = "Kicked user ${message.replyToMessage.from.getFormattedName()}", c = message.chat)
    }
}