package com.miche.krak.kBot.commands.core

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender

interface CallbackHolder {

    fun processCallback(absSender : AbsSender, callback : CallbackQuery)

    fun getId() : String

    fun getLabel() : String

    fun getButton() : InlineKeyboardButton
}