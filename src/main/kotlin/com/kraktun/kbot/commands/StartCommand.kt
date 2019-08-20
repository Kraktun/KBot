package com.kraktun.kbot.commands

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.database.DatabaseManager
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.isGroupOrSuper
import com.kraktun.kbot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * Start command: adds the user/group to the DB and adds admins.
 */
class StartCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/start",
        description = "Start the bot. If used in a group, add/reset admins.",
        targets = listOf(Pair(Target.USER, Status.NOT_REGISTERED),
            Pair(Target.GROUP, Status.NOT_REGISTERED),
            Pair(Target.SUPERGROUP, Status.NOT_REGISTERED)),
        exe = this
    )

    override fun execute(absSender: AbsSender, message: Message) {
        val chatId = message.chatId
        if (message.chat.isUserChat && DatabaseManager.getUser(message.from.id) == null) // Add only if not present, or it will overwrite current value
            DatabaseManager.addUser(user = message.from, userStatus = Status.USER)
        else if (message.chat.isGroupOrSuper()) {
            // If it's a group insert the group and add the admins as admin
            if (!DatabaseManager.groupExists(chatId)) {
                DatabaseManager.addGroup(chatId)
            } else {
                // Reset old admins
                DatabaseManager.updateGroupUsersStatus(groupId = chatId, oldStatus = Status.ADMIN, newStatus = Status.USER)
            }
            val getAdmins = GetChatAdministrators()
            getAdmins.chatId = chatId.toString()
            try {
                val admins = absSender.execute(getAdmins)
                DatabaseManager.addGroupUsers(groupId = chatId, usersId = admins.map { admin -> admin.user.id }, statusK = Status.ADMIN)
            } catch (e: TelegramApiException) {
                simpleMessage(absSender, "An error occurred: ${e.message}", chatId)
                e.printStackTrace()
            }
        }
        simpleMessage(absSender, "Welcome to me", chatId)
    }
}