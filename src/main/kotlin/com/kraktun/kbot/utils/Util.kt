package com.kraktun.kbot.utils

import com.kraktun.kbot.data.Configurator
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

/**
 * Execute a generic method, catching the exceptions
 */
fun <T : java.io.Serializable> executeMethod(absSender: AbsSender, m: BotApiMethod<T>): T? {
    return try {
        absSender.execute(m)
    } catch (e: TelegramApiException) {
        if (Configurator.isInitialized()) Configurator.log(e)
        null
    }
}

/**
 * Get keyboard with buttons from a list.
 * Optional: define how many buttons for row (default = 1).
 */
fun getSimpleListKeyboard(list: List<Any>, buttonsInRow: Int = -1): ReplyKeyboardMarkup {
    val key = ReplyKeyboardMarkup()
    if (buttonsInRow <= 0) {
        key.keyboard.addAll(
            list.map {
                val row = KeyboardRow()
                row.add(it.toString())
                row
            }
        )
    } else {
        val listHolder = mutableListOf<List<Any>>()
        for (counter in 0 until buttonsInRow) {
            listHolder.add(
                list.filter {
                    list.indexOf(it) / buttonsInRow == counter
                }
            )
        }
        key.keyboard.addAll(
            listHolder.map {
                val row = KeyboardRow()
                row.addAll(it.map { button -> button.toString() })
                row
            }
        )
    }
    key.resizeKeyboard = true
    return key
}
