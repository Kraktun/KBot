package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
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

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        if (chat.isUserChat && DatabaseManager.getUser(user.id) == null) // Add only if not present, or it will overwrite current value
            DatabaseManager.addUser(user = user, userStatus = Status.USER)
        else if (chat.isGroupChat || chat.isSuperGroupChat) {
            // If it's a group insert the group and add the admins as admin
            if (!DatabaseManager.groupExists(chat.id)) {
                DatabaseManager.addGroup(chat.id)
            } else {
                // Reset old admins
                DatabaseManager.updateGroupUsersStatus(groupId = chat.id, oldStatus = Status.ADMIN, newStatus = Status.USER)
            }
            val getAdmins = GetChatAdministrators()
            getAdmins.chatId = chat.id.toString()
            try {
                val admins = absSender.execute(getAdmins)
                DatabaseManager.addGroupUsers(groupId = chat.id, usersId = admins.map { admin -> admin.user.id }, statusK = Status.ADMIN)
            } catch (e: TelegramApiException) {
                simpleMessage(absSender, "An error occurred: ${e.message}", chat)
                e.printStackTrace()
            }
        }
        simpleMessage(absSender, "Welcome to me", chat)
    }
}