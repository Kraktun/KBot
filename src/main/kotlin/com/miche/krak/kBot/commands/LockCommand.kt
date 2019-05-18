package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.utils.GroupStatus
import com.miche.krak.kBot.utils.Status
import com.miche.krak.kBot.utils.Target
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class LockCommand : CommandInterface {

    val engine = BaseCommand(
        command = "lock",
        description = "Lock the group: only admins can send messages",
        targets = listOf(Target.GROUP, Target.SUPERGROUP),
        privacy = Status.ADMIN,
        argsNum = 0,
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        DatabaseManager.instance.updateGroupStatus(chat.id, GroupStatus.LOCKED)
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        answer.text = "Group is locked. Only admins can send messages"
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }
}