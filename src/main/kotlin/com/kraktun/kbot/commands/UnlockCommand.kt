package com.kraktun.kbot.commands

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.database.DatabaseManager
import com.kraktun.kbot.objects.GroupStatus
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.simpleMessage
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