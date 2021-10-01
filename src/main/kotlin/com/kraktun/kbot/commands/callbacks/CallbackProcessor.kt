package com.kraktun.kbot.commands.callbacks

import com.kraktun.kbot.jobs.JobInfo
import com.kraktun.kbot.jobs.JobTask
import com.kraktun.kutils.other.readInLock
import com.kraktun.kutils.other.writeInLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender
import java.time.Instant
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * Process a callback.
 *
 */
object CallbackProcessor {

    @Volatile private var list = mutableListOf<CallbackChat>()
    private val lock = ReentrantReadWriteLock()

    /**
     * Execute methods defined for a callback.
     * @param absSender absSender
     * @param user user who clicked on the button with the callback
     * @param chat where the user clicked
     * @param callback callback from the update
     * @return true if a callback exists, false otherwise
     */
    fun fireCallback(absSender: AbsSender, user: Long, chat: Long, callback: CallbackQuery): Boolean {
        return if (callback.data.isNotEmpty()) {
            lock.readInLock {
                val c = list.find {
                    it.callback.id == callback.data &&
                        (it.user == user || it.user == -1L) &&
                        it.chat == chat
                }
                if (c != null) {
                    c.callback.onCallbackFired(absSender, callback)
                    val text = c.callback.getCallbackMessage(callback)
                    checkMessageLength(text)
                    c.callback.answerCallback(absSender, text, callback.id)
                    true
                } else {
                    false
                }
            }
        } else false
    }

    /**
     * Register a listener for a callback.
     * @param user user who is allowed to fire tha callback. -1 to allow everybody
     * @param chat chat where the callback is defined
     * @param callbackHolder methods to execute
     */
    fun insertCallback(user: Long, chat: Long, callbackHolder: CallbackHolder) {
        lock.writeInLock {
            list.add(CallbackChat(callbackHolder, user, chat))
        }
    }

    /**
     * Remove a listener for a callback
     */
    fun removeCallback(user: Long, callbackHolderId: String) {
        lock.writeInLock {
            list.removeIf { it.callback.id == callbackHolderId && it.user == user }
        }
    }

    /**
     * Remove a listener for a callback
     */
    fun removeCallback(user: Long, chat: Long, callbackHolderId: String) {
        lock.writeInLock {
            list.removeIf { it.callback.id == callbackHolderId && it.user == user && it.chat == chat }
        }
    }

    class CleanerJob : JobTask() {

        companion object {
            val jobInfo = JobInfo(
                key = "CALLBACKCLEANER",
                interval = 5, // seconds
                initialDelay = 10
            )
        }

        override fun execute(scope: CoroutineScope) {
            val now = Instant.now()
            scope.launch {
                lock.writeInLock {
                    list.filter {
                        it.callback.time.plusSeconds(it.callback.ttl).isBefore(now)
                    }.forEach {
                        list.removeIf { f ->
                            it.callback.id == f.callback.id &&
                                it.chat == f.chat &&
                                it.user == f.user
                        }
                    }
                }
            }
        }
    }
}
