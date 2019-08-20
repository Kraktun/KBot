package com.kraktun.kbot.commands

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.ChatOptions
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.commands.core.FilterResult
import com.kraktun.kbot.database.DatabaseManager
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.*
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * With current API there is no way to get a user id from a mention thus ban by mentions are not available.
 * Command must be a reply to a message from the user to ban, and optionally can include a time (in hours) to limit the ban.
 */
class BanCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/ban",
        description = "Reply a user to ban him and prevent him from accessing the group again forever. Optional time limit (hours) can be passed.",
        targets = listOf(
            Pair(Target.GROUP, Status.ADMIN),
            Pair(Target.SUPERGROUP, Status.ADMIN)),
        filterFun = { m: Message ->
            m.isReply
        },
        chatOptions = listOf(ChatOptions.BOT_IS_ADMIN, ChatOptions.OPTION_ALL_USER_ADMIN_DISABLED),
        onError = { absSender, m, result ->
            if (result == FilterResult.INVALID_STATUS)
                simpleMessage(absSender = absSender, s = "Yeah, as if you could...", c = m.chat)
        },
        exe = this
    )

    override fun execute(absSender: AbsSender, message: Message) {
        if (message.chat.isGroupChat && DatabaseManager.getGroupUserStatus(message.chatId, message.replyToMessage.from.id) == Status.ADMIN) {
            simpleMessage(absSender, "Admins can be removed only by the creator", message.chat)
            return
        }
        // DB will be re-added when I'll figure out how to manage temporary bans.
        // DatabaseManager.addGroupUser(groupId = chat.id, userId = message.replyToMessage.from.id, statusK = Status.BANNED)
        val date = message.arguments().ifNotEmpty({
            (message.arguments()[0].toDouble() * 3600).toInt() }, // hours to seconds
            0) as Int
        kickUser(absSender = absSender, u = message.replyToMessage.from, c = message.chat, date = date)
        val until = if (date > 0) "for ${message.arguments()[0]} hours." else "forever."
        simpleMessage(absSender = absSender, s = "Banned user ${message.replyToMessage.from.getFormattedName()} $until", c = message.chat)
    }
}