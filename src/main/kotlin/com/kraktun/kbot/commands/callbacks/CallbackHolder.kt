package com.kraktun.kbot.commands.callbacks

import com.kraktun.kbot.utils.executeMethod
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender
import java.time.Instant

/**
 * Stores data used while processing the callback.
 * Also defines methods to invoke when firing the callback.
 */
interface CallbackHolder {

    val id: String // callback data, used as a ID. Must be unique.
    val label: String // string to show in the button
    val time: Instant get() = Instant.now()
    val ttl: Long // how long must the callback be kept alive (i.e. clickable)
    val button: InlineKeyboardButton
        get() {
            return InlineKeyboardButton().apply {
                text = label
                callbackData = id
            }
        } // return button with label and data

    // return message to send to the user. Override answerCallback to customize it.
    val processCallback: (absSender: AbsSender, callback: CallbackQuery) -> String

    // Override this if you want to send a message with custom options (e.g. as an alert)
    val answerCallback: (absSender: AbsSender, message: String, callbackId: String) -> Unit
        get() = { absSender, message, callbackId ->
            val answer = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackId)
                .text(message)
                .build()
            executeMethod(absSender = absSender, m = answer)
        }
    // toggle button label
    val changeLabel: (absSender: AbsSender, callback: CallbackQuery, newLabel: String) -> Unit
        get() = { absSender, callback, newLabel ->
            val key = callback.message.replyMarkup.keyboard
            key.forEach { ex ->
                ex.forEach {
                    if (it.callbackData == id)
                        it.text = newLabel
                }
            }
            val e = EditMessageReplyMarkup.builder()
                .replyMarkup(InlineKeyboardMarkup(key))
                .messageId(callback.message.messageId)
                .chatId(callback.message.chatId.toString())
                .build()
            executeMethod(absSender = absSender, m = e)
        }
}
