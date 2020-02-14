package com.kraktun.kbot.commands.callbacks

import com.kraktun.kbot.jobs.JobInfo
import com.kraktun.kutils.other.readInLock
import com.kraktun.kutils.other.writeInLock
import java.time.Instant
import java.util.concurrent.locks.ReentrantReadWriteLock
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender

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
    fun fireCallback(absSender: AbsSender, user: Int, chat: Long, callback: CallbackQuery): Boolean {
        return if (callback.data.isNotEmpty()) {
            lock.readInLock {
                val c = list.find { it.callback.id == callback.id &&
                        (it.user == user || it.user == -1) &&
                        it.chat == chat }
                if (c != null) {
                    val text = c.callback.processCallback(absSender, callback)
                    c.callback.answerCallback(absSender, text)
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
    fun insertCallback(user: Int, chat: Long, callbackHolder: CallbackHolder) {
        lock.writeInLock {
            list.add(CallbackChat(callbackHolder, user, chat))
        }
    }

    /**
     * Remove a listener for a callback
     */
    fun removeCallback(user: Int, callbackHolderId: String) {
        lock.writeInLock {
            list.removeIf { it.callback.id == callbackHolderId && it.user == user }
        }
    }

    /**
     * Remove a listener for a callback
     */
    fun removeCallback(chat: Long, callbackHolderId: String) {
        lock.writeInLock {
            list.removeIf { it.callback.id == callbackHolderId && it.chat == chat }
        }
    }

    /**
     * Remove a listener for a callback
     */
    fun removeCallback(user: Int, chat: Long, callbackHolderId: String) {
        lock.writeInLock {
            list.removeIf { it.callback.id == callbackHolderId && it.user == user && it.chat == chat }
        }
    }

    class CleanerJob : InterruptableJob {

        companion object {
            val jobInfo = JobInfo(
                name = "CALLBACKCLEANER",
                interval = 5, // seconds
                trigger = "CALLBACKCLEANER_TRIGGER",
                group = "jobs",
                delay = 10)
        }

        @Throws(JobExecutionException::class)
        override fun execute(context: JobExecutionContext) {
            val now = Instant.now()
            lock.writeInLock {
                list.filter {
                    it.callback.time.plusSeconds(it.callback.TTL).isBefore(now)
                }.forEach {
                    list.removeIf { f -> it.callback.id == f.callback.id &&
                            it.chat == f.chat &&
                            it.user == f.user }
                }
            }
        }

        override fun interrupt() { }
    }
}
