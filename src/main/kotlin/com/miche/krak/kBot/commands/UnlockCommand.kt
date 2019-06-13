package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.objects.GroupStatus
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Unlocks a group:
 * all members can send messages again.
 */
class UnlockCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/unlock",
        description = "Unlock the group",
        targets = listOf(Pair(Target.GROUP, Status.ADMIN),
            Pair(Target.SUPERGROUP, Status.ADMIN)),
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        DatabaseManager.updateGroup(chat.id, GroupStatus.NORMAL)
        simpleMessage(absSender, "Group is unlocked. Messages are allowed again", chat)
    }
}