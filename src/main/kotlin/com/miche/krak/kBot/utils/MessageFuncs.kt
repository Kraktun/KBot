package com.miche.krak.kBot.utils

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

private const val TAG = "MESSAGEFUNCS"

fun deleteMessage(absSender: AbsSender, m : Message) {
    val message = DeleteMessage()
        .setChatId(m.chatId)
        .setMessageId(m.messageId)
    try {
        absSender.execute(message)
    } catch (e: TelegramApiException) {
        logK(TAG, e)
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
        logK(TAG, e)
        e.printStackTrace()
    }
}

/**
 * Kick a user from a chat and optionally ban him for a limited (if date >= 0)  or unlimited (if date = 0) amount of time
 */
fun kickUser(absSender: AbsSender, u : User, c : Chat, date : Int = -1) {
    var message = KickChatMember()
        .setChatId(c.id)
        .setUserId(u.id)
    if (date >= 0)
        message = message.setUntilDate(date)
    try {
        absSender.execute(message)
    } catch (e: TelegramApiException) {
        logK(TAG, e)
        e.printStackTrace()
    }
}

fun executeMethod(absSender: AbsSender, m : BotApiMethod<Boolean>) {
    try {
        absSender.execute(m)
    } catch (e: TelegramApiException) {
        logK(TAG, e)
        e.printStackTrace()
    }
}