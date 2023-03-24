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
abstract class CallbackHolder {

    abstract val id: String // callback data, used as a ID. Must be unique.
    abstract var label: String // string to show in the button
    val time: Instant get() = Instant.now()
    abstract val ttl: Long // how long must the callback be kept alive (i.e. clickable)
    abstract var resultAsAlert: Boolean // show result as alert
    abstract var resultAsUrl: Boolean // true to send the message from getCallbackMessage as an url
    val button: InlineKeyboardButton
        get() {
            return InlineKeyboardButton().apply {
                text = label
                callbackData = id
            }
        } // return button with label and data

    // return message to send to the user.
    abstract val getCallbackMessage: (callback: CallbackQuery) -> String

    val answerCallback: (absSender: AbsSender, message: String, callbackId: String) -> Unit
        get() = { absSender, message, callbackId ->
            var answerBuilder = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackId)
                .showAlert(resultAsAlert)
            answerBuilder = if (resultAsUrl) {
                answerBuilder.url(message)
            } else {
                answerBuilder.text(message)
            }
            val answer = answerBuilder.build()
            executeMethod(absSender = absSender, m = answer)
        }

    // function to execute before the callback is answered
    abstract fun onCallbackFired(absSender: AbsSender, callback: CallbackQuery)

    // function to execute when the ttl time expires
    abstract fun onTtlExpired(absSender: AbsSender)

    // toggle button label
    protected val changeLabel: (absSender: AbsSender, callback: CallbackQuery, newLabel: String) -> Unit
        get() = { absSender, callback, newLabel ->
            val key = callback.message.replyMarkup.keyboard
            key.forEach { ex ->
                ex.forEach {
                    if (it.callbackData == id) {
                        it.text = newLabel
                    }
                }
            }
            val e = EditMessageReplyMarkup.builder()
                .replyMarkup(InlineKeyboardMarkup(key))
                .messageId(callback.message.messageId)
                .chatId(callback.message.chatId.toString())
                .build()
            executeMethod(absSender = absSender, m = e)
            this.label = newLabel
        }
}
