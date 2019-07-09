package com.miche.krak.kBot.utils

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.time.Instant

private const val TAG = "MESSAGEFUNCS"

fun deleteMessage(absSender: AbsSender, m: Message) {
    val message = DeleteMessage()
        .setChatId(m.chatId)
        .setMessageId(m.messageId)
    executeMethod(absSender, message)
}

/**
 * Send a simple message
 */
fun simpleMessage(absSender: AbsSender, s: String, c: Chat) {
    val message = SendMessage()
        .setChatId(c.id)
        .setText(s)
    executeMethod(absSender, message)
}

/**
 * Send a simple message
 */
fun simpleHTMLMessage(absSender: AbsSender, s: String, c: Chat) {
    val message = SendMessage()
        .setChatId(c.id)
        .setText(s)
        .enableHtml(true)
    executeMethod(absSender, message)
}

/**
 * Send a simple message
 */
fun simpleMessage(absSender: AbsSender, s: String, c: Long) {
    val message = SendMessage()
        .setChatId(c)
        .setText(s)
    executeMethod(absSender, message)
}

/**
 * Send a simple message
 */
fun simpleHTMLMessage(absSender: AbsSender, s: String, c: Long) {
    val message = SendMessage()
        .setChatId(c)
        .setText(s)
        .enableHtml(true)
    executeMethod(absSender, message)
}

/**
 * Kick a user from a chat and optionally ban him for a limited (if date >= 0)  or unlimited (if date = 0) amount of time
 */
fun kickUser(absSender: AbsSender, u: User, c: Chat, date: Int = -1) {
    var message = KickChatMember()
        .setChatId(c.id)
        .setUserId(u.id)
    if (date >= 0)
        message = message.setUntilDate(Instant.now().plusSeconds(date.toLong()))
    executeMethod(absSender, message)
}

/**
 * Send a custom keyboard for the user to choose
 */
fun sendKeyboard(absSender: AbsSender, c: Chat, s: String, keyboard: ReplyKeyboard) {
    insertKeyboard(absSender, c, s, keyboard)
}

fun sendSimpleListKeyboard(absSender: AbsSender, c: Chat, s: String, list: List<Any>) {
    val key = ReplyKeyboardMarkup()
    key.keyboard.addAll(list.map {
        val row = KeyboardRow()
        row.add(it.toString())
        row
    })
    key.resizeKeyboard = true
    insertKeyboard(absSender, c, s, key)
}

/**
 * Private method to send\remove keyboards
 */
private fun insertKeyboard(absSender: AbsSender, c: Chat, s: String, keyboard: ReplyKeyboard) {
    val message = SendMessage()
        .setChatId(c.id)
        .setText(s)
        .setReplyMarkup(keyboard)
    executeMethod(absSender, message)
}

/**
 * Remove a keyboard restoring normal keyboard
 */
fun removeKeyboard(absSender: AbsSender, c: Chat, s: String) {
    insertKeyboard(absSender, c, s, ReplyKeyboardRemove())
}

/**
 * Execute a generic method, catching th exceptions
 */
fun <T : java.io.Serializable> executeMethod(absSender: AbsSender, m: BotApiMethod<T>): T? {
    return try {
        absSender.execute(m)
    } catch (e: TelegramApiException) {
        logK(TAG, e)
        e.printStackTrace()
        null
    }
}
