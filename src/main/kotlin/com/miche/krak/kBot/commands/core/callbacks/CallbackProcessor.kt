package com.miche.krak.kBot.commands.core.callbacks

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Class to store handlers for callbacks.
 * As of now only callbacks in private chats can be managed in this way.
 * TODO THis needs a cleaner (job)
 */
object CallbackProcessor {

    @Volatile private var list = mutableListOf<Pair<CallbackHolder, Int>>()

    fun fireCallback(absSender: AbsSender, user: Int, callback: CallbackQuery): Boolean {
        return if (callback.data.isNotEmpty()) {
            synchronized(this) {
                list.find { it.first.getId() == callback.data && it.second == user }?.first?.processCallback(absSender, callback) != null
            }
        } else false
    }

    /**
     * Register a listener for a callback
     */
    fun insertCallback(user: Int, callbackHolder: CallbackHolder) {
        synchronized(this) {
            list.add(Pair(callbackHolder, user))
        }
    }

    /**
     * Remove a listener for a callback
     */
    fun removeCallback(user: Int, callbackHolderId: String) {
        // printlnK("CALLBACK PROCESSOR", "Deleting callback ($callbackHolderId)")
        synchronized(this) {
            list.removeIf { it.first.getId() == callbackHolderId && it.second == user }
        }
    }
}