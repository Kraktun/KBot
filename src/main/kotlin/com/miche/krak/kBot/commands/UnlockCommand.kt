package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.objects.GroupStatus
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

/**
 * Unlocks a group:
 * all members can send messages again.
 */
class UnlockCommand : CommandInterface {

    val engine = BaseCommand(
        command = "unlock",
        description = "Unlock the group",
        targets = listOf(Target.GROUP, Target.SUPERGROUP),
        privacy = Status.ADMIN,
        argsNum = 0,
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        DatabaseManager.updateGroupStatus(chat.id, GroupStatus.NORMAL)
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        answer.text = "Group is unlocked. Messages are allowed again"
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }
}