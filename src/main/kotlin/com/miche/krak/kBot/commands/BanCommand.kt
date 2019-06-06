package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
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

class BanCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/ban",
        description = "Ban the user and prevent him from accessing the group again",
        targets = listOf(
            Pair(Target.GROUP, Status.ADMIN),
            Pair(Target.SUPERGROUP, Status.ADMIN)),
        argsNum = 0,
        filterFun = { m : Message ->
            m.isReply
        },
        exe = this
    )


    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        DatabaseManager.addGroupUser(groupId = chat.id, userId = message.replyToMessage.from.id, statusK = Status.BANNED)
        kickUser(absSender = absSender, u = user, c = chat)
        simpleMessage(absSender = absSender, s = "Banned user ${getQualifiedUser(user)}", c = chat)
    }
}