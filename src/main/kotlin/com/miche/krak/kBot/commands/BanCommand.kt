package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.getQualifiedUser
import com.miche.krak.kBot.utils.ifNotEmpty
import com.miche.krak.kBot.utils.kickUser
import com.miche.krak.kBot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
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
        argsNum = 0,
        filterFun = { m : Message ->
            m.isReply
        },
        onError = { absSender, _,  m ->
            if ((m.chat.isSuperGroupChat || m.chat.isGroupChat) && DatabaseManager.getGroupUserStatus(m.chatId, m.from.id) < Status.ADMIN)
                simpleMessage(absSender = absSender, s = "Yeah, as if you could...", c = m.chat)
        },
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        if (chat.isGroupChat && DatabaseManager.getGroupUserStatus(chat.id, message.replyToMessage.from.id) == Status.ADMIN) {
            simpleMessage(absSender, "Admins can be removed only by the creator", chat)
            return
        }
        //DB will be re-added when I'll figure out how to manage temporary bans.
        //DatabaseManager.addGroupUser(groupId = chat.id, userId = message.replyToMessage.from.id, statusK = Status.BANNED)
        val date = arguments.ifNotEmpty({
            (arguments[0].toDouble() * 3600).toInt()} //hours to seconds
            , 0) as Int
        kickUser(absSender = absSender, u = message.replyToMessage.from, c = chat, date = date)
        val until = if (date > 0) "for ${arguments[0]} hours." else "forever."
        simpleMessage(absSender = absSender, s = "Banned user ${getQualifiedUser(message.replyToMessage.from)} $until", c = chat)
    }
}