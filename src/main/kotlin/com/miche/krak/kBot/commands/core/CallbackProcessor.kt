package com.miche.krak.kBot.commands.core

import com.miche.krak.kBot.utils.printlnK
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender


object CallbackProcessor {

    @Volatile private var list = mutableListOf<CallbackHolder>()

    fun fireCallback(absSender: AbsSender, callback : CallbackQuery) : Boolean {
        return if (callback.data.isNotEmpty()) {
            synchronized(this) {
                list.find { it.getId() == callback.data }?.processCallback(absSender, callback) != null
            }
        } else false
    }

    fun insertCallback(callbackHolder: CallbackHolder) {
        synchronized(this) {
            list.add(callbackHolder)
        }
    }

    fun removeCallback(callbackHolderId : String) {
        printlnK("CALLBACK PROCESSOR", "Deleting callback ($callbackHolderId)")
        synchronized(this) {
            list.removeIf { it.getId() ==  callbackHolderId}
        }
    }

}