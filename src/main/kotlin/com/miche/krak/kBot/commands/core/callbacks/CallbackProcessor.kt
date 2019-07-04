package com.miche.krak.kBot.commands.core.callbacks

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Class to store handlers for callbacks.
 * Callbacks holders are objects and not static classes.
 */
object CallbackProcessor {

    @Volatile private var list = mutableListOf<CallbackHolder>()

    fun fireCallback(absSender: AbsSender, callback: CallbackQuery): Boolean {
        return if (callback.data.isNotEmpty()) {
            synchronized(this) {
                list.find { it.getId() == callback.data }?.processCallback(absSender, callback) != null
            }
        } else false
    }

    /**
     * Register a listener for a callback
     */
    fun insertCallback(callbackHolder: CallbackHolder) {
        synchronized(this) {
            list.add(callbackHolder)
        }
    }

    /**
     * Remove a listener for a callback
     */
    fun removeCallback(callbackHolderId: String) {
        // printlnK("CALLBACK PROCESSOR", "Deleting callback ($callbackHolderId)")
        synchronized(this) {
            list.removeIf { it.getId() == callbackHolderId }
        }
    }
}