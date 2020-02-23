package com.kraktun.kbot.commands.callbacks

import com.kraktun.kbot.utils.executeMethod
import java.time.Instant
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender

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
    val answerCallback: (absSender: AbsSender, message: String) -> Unit
        get() = { absSender, message ->
            val answer = AnswerCallbackQuery()
            answer.callbackQueryId = id
            answer.text = message
            executeMethod(absSender = absSender, m = answer)
        }
}
