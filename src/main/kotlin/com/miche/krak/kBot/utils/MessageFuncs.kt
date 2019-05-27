package com.miche.krak.kBot.utils

import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

fun deleteMessage(absSender: AbsSender, m : Message) {
    val message = DeleteMessage()
        .setChatId(m.chatId)
        .setMessageId(m.messageId)
    try {
        absSender.execute(message)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}

fun simpleMessage(absSender: AbsSender, s : String, c : Chat) {
    val message = SendMessage()
        .setChatId(c.id)
        .setText(s)
    try {
        absSender.execute(message)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}

fun kickUser(absSender: AbsSender, u : User, c : Chat) {
    val message = KickChatMember()
        .setChatId(c.id)
        .setUserId(u.id)
    try {
        absSender.execute(message)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}