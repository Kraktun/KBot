package com.miche.krak.kBot.commands.core.callbacks

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Interface to process a callback query
 */
interface CallbackHolder {

    fun processCallback(absSender: AbsSender, callback: CallbackQuery)

    fun getId(): String // callback data, used as a ID. Must be unique.

    fun getLabel(): String // callback string to show in the button

    fun getButton(): InlineKeyboardButton // return button with label and data
}