package com.kraktun.kbot.commands.callbacks

/**
 * Same as a Triple, but with significant names
 */
data class CallbackChat(val callback: CallbackHolder, val user: Int, val chat: Long)
