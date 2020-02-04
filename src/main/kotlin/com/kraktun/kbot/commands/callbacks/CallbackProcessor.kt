package com.kraktun.kbot.commands.callbacks

import com.kraktun.kutils.other.readInLock
import com.kraktun.kutils.other.writeInLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Class to store handlers for callbacks.
 * As of now only callbacks in private chats can be managed in this way.
 * TODO This needs a cleaner (job)
 */
object CallbackProcessor {

    @Volatile private var list = mutableListOf<Pair<CallbackHolder, Int>>()
    private val lock = ReentrantReadWriteLock()

    fun fireCallback(absSender: AbsSender, user: Int, callback: CallbackQuery): Boolean {
        return if (callback.data.isNotEmpty()) {
            lock.readInLock {
                list.find { it.first.getId() == callback.data && it.second == user }?.first?.processCallback(absSender, callback) != null
            }
        } else false
    }

    /**
     * Register a listener for a callback
     */
    fun insertCallback(user: Int, callbackHolder: CallbackHolder) {
        lock.writeInLock {
            list.add(Pair(callbackHolder, user))
        }
    }

    /**
     * Remove a listener for a callback
     */
    fun removeCallback(user: Int, callbackHolderId: String) {
        // printlnK("CALLBACK PROCESSOR", "Deleting callback ($callbackHolderId)")
        lock.writeInLock {
            list.removeIf { it.first.getId() == callbackHolderId && it.second == user }
        }
    }
}
