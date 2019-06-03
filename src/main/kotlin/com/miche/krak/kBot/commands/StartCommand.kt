package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message


/**
 * Start command: adds the user/group to the DB and adds admins.
 */
class StartCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/start",
        description = "Start the bot",
        targets = listOf(Pair(Target.USER, Status.NOT_REGISTERED),
            Pair(Target.GROUP, Status.NOT_REGISTERED),
            Pair(Target.SUPERGROUP, Status.NOT_REGISTERED)),
        argsNum = 0,
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        if (chat.isUserChat && DatabaseManager.getUser(user.id) == null) //Add only if not present, or it will overwrite current value
            DatabaseManager.insertUser(user = user, userStatus = Status.USER)
        else if (chat.isGroupChat && !DatabaseManager.groupExists(chat.id)) {
            //If it's a group insert the group and add the admins as admin
            if (!DatabaseManager.groupExists(chat.id)) {
                DatabaseManager.insertGroup(chat.id)
                val getAdmins = GetChatAdministrators()
                getAdmins.chatId = chat.id.toString()
                try {
                    val admins = absSender.execute(getAdmins)
                    DatabaseManager.addGroupAdmins(groupId = chat.id, admins = admins.map { admin -> admin.user.id })
                } catch (e: TelegramApiException) {
                    val error = SendMessage()
                    error.chatId = chat.id.toString()
                    error.text = "An error occurred: ${e.message}"
                    try {
                        absSender.execute(error)
                    } catch (ee: TelegramApiException) {
                        ee.printStackTrace()
                    }
                    e.printStackTrace()
                }
            }
        }
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        answer.text = "Welcome to me"
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }
}