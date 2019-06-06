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
 * Command to lock a group:
 * in a locked group only admins can send messages.
 * Note that the logic to delete messages must be implemented in the onUpdateReceived() method,
 * as this command changes only the status of the group.
 */
class LockCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/lock",
        description = "Lock the group: only admins can send messages",
        targets = listOf(Pair(Target.GROUP, Status.ADMIN),
            Pair(Target.SUPERGROUP, Status.ADMIN)),
        argsNum = 0,
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        DatabaseManager.updateGroup(chat.id, GroupStatus.LOCKED)
        simpleMessage(absSender = absSender, s = "Group is locked. Only admins can send messages", c = chat)
    }
}