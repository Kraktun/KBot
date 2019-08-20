package com.kraktun.kbot.commands

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.ChatOptions
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.database.DatabaseManager
import com.kraktun.kbot.objects.GroupStatus
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.objects.Message
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
        chatOptions = mutableListOf(ChatOptions.BOT_IS_ADMIN),
        exe = this
    )

    override fun execute(absSender: AbsSender, message: Message) {
        DatabaseManager.updateGroup(message.chatId, GroupStatus.LOCKED)
        simpleMessage(absSender = absSender, s = "Group is locked. Only admins can send messages", c = message.chat)
    }
}