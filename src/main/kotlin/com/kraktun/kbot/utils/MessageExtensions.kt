package com.kraktun.kbot.utils

import java.time.Instant
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.meta.bots.AbsSender

fun AbsSender.deleteMessage(m: Message) {
    val message = DeleteMessage()
        .setChatId(m.chatId)
        .setMessageId(m.messageId)
    executeMethod(this, message)
}

/**
 * Send a simple message
 */
fun AbsSender.simpleMessage(s: String, c: Chat, enableHtml: Boolean = false) {
    val message = SendMessage()
        .setChatId(c.id)
        .setText(s)
        .enableHtml(enableHtml)
    executeMethod(this, message)
}

/**
 * Send a simple message
 */
fun AbsSender.simpleMessage(s: String, c: Long, enableHtml: Boolean = false) {
    val message = SendMessage()
        .setChatId(c)
        .setText(s)
        .enableHtml(enableHtml)
    executeMethod(this, message)
}

/**
 * Kick a user from a chat and optionally ban him for a limited (if date >= 0)  or unlimited (if date = 0) amount of time
 */
fun AbsSender.kickUser(u: User, c: Chat, date: Int = -1) {
    var message = KickChatMember()
        .setChatId(c.id)
        .setUserId(u.id)
    if (date >= 0)
        message = message.setUntilDate(Instant.now().plusSeconds(date.toLong()))
    executeMethod(this, message)
}

/**
 * Send a custom keyboard for the user to choose
 */
fun AbsSender.sendKeyboard(c: Chat, s: String, keyboard: ReplyKeyboard) {
    insertKeyboard(c, s, keyboard)
}

/**
 * Private method to send\remove keyboards
 */
private fun AbsSender.insertKeyboard(c: Chat, s: String, keyboard: ReplyKeyboard) {
    val message = SendMessage()
        .setChatId(c.id)
        .setText(s)
        .setReplyMarkup(keyboard)
    executeMethod(this, message)
}

/**
 * Remove a keyboard restoring normal keyboard
 */
fun AbsSender.removeKeyboard(c: Chat, s: String) {
    insertKeyboard(c, s, ReplyKeyboardRemove())
}
