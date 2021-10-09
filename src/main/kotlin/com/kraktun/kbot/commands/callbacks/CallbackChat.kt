package com.kraktun.kbot.commands.callbacks

import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Same as a Triple, but with significant names
 */
data class CallbackChat(val absSender: AbsSender, val callback: CallbackHolder, val user: Long, val chat: Long)
