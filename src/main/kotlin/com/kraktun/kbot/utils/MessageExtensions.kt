package com.kraktun.kbot.utils

import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.meta.bots.AbsSender
import java.time.Instant

/**
 * Delete a message.
 * @return true if success, null if an exception was thrown
 */
fun AbsSender.deleteMessage(m: Message): Boolean? {
    val message = DeleteMessage.builder()
        .chatId(m.chatId.toString())
        .messageId(m.messageId)
        .build()
    return executeMethod(this, message)
}

/**
 * Send a simple message
 * @return message, null if an exception was thrown
 */
fun AbsSender.simpleMessage(s: String, c: Chat, enableHtml: Boolean = false): Message? {
    val message = SendMessage.builder()
        .chatId(c.id.toString())
        .text(s)
        .build()
    message.enableHtml(enableHtml)
    return executeMethod(this, message)
}

/**
 * Send a simple message
 * @return message, null if an exception was thrown
 */
fun AbsSender.simpleMessage(s: String, c: Long, enableHtml: Boolean = false): Message? {
    val message = SendMessage.builder()
        .chatId(c.toString())
        .text(s)
        .build()
    message.enableHtml(enableHtml)
    return executeMethod(this, message)
}

/**
 * Kick a user from a chat and optionally ban him for a limited (if date >= 0)  or unlimited (if date = 0) amount of time.
 * @return true if success, null if an exception was thrown
 */
fun AbsSender.kickUser(u: User, c: Chat, date: Int = -1): Boolean? {
    val message = BanChatMember.builder()
        .chatId(c.id.toString())
        .userId(u.id)
        .build()
    if (date >= 0)
        message.untilDate = Instant.now().plusSeconds(date.toLong()).epochSecond.toInt()
    return executeMethod(this, message)
}

/**
 * Send a custom keyboard for the user to choose
 * @return message, null if an exception was thrown
 */
fun AbsSender.sendKeyboard(s: String, c: Chat, keyboard: ReplyKeyboard, enableHtml: Boolean = false): Message? {
    return insertKeyboard(s, c, keyboard, enableHtml)
}

/**
 * Private method to send\remove keyboards
 * @return message, null if an exception was thrown
 */
private fun AbsSender.insertKeyboard(s: String, c: Chat, keyboard: ReplyKeyboard, enableHtml: Boolean = false): Message? {
    val message = SendMessage.builder()
        .chatId(c.id.toString())
        .text(s)
        .replyMarkup(keyboard)
        .build()
    message.enableHtml(enableHtml)
    return executeMethod(this, message)
}

/**
 * Remove a keyboard restoring normal keyboard
 * @return message, null if an exception was thrown
 */
fun AbsSender.removeKeyboard(s: String, c: Chat, enableHtml: Boolean = false): Message? {
    return insertKeyboard(s, c, ReplyKeyboardRemove().apply { removeKeyboard = true }, enableHtml)
}
