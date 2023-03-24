package com.kraktun.kbot.utils

import com.kraktun.kbot.data.Configurator
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import kotlin.math.ceil

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
 * Optional: define how many buttons for each row (default = 1).
 */
fun getSimpleListKeyboard(list: List<Any>, buttonsInRow: Int = 1): ReplyKeyboardMarkup {
    val key = ReplyKeyboardMarkup()
    val repartitionedList = mutableListOf<KeyboardRow>()
    for (rowNum in 0 until ceil(list.size.toDouble() / buttonsInRow).toInt()) {
        val row = KeyboardRow()
        row.addAll(
            list.filter {
                list.indexOf(it) / buttonsInRow == rowNum
            }.map { it.toString() },
        )
        repartitionedList.add(row)
    }
    key.keyboard = repartitionedList
    key.resizeKeyboard = true
    key.oneTimeKeyboard = true
    return key
}

/**
 * Get inline keyboard to append to a message.
 * Optional: define how many buttons per row (default = 1).
 */
fun getSimpleInlineKeyboard(list: List<InlineKeyboardButton>, buttonsInRow: Int = 1): InlineKeyboardMarkup {
    val key = InlineKeyboardMarkup()
    val repartitionedList = mutableListOf<List<InlineKeyboardButton>>()
    for (rowNum in 0 until ceil(list.size.toDouble() / buttonsInRow).toInt()) {
        val row = mutableListOf<InlineKeyboardButton>()
        row.addAll(
            list.filter {
                list.indexOf(it) / buttonsInRow == rowNum
            },
        )
        repartitionedList.add(row)
    }
    return key
}
