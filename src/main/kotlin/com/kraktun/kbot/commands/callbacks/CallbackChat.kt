package com.kraktun.kbot.commands.callbacks

/**
 * Same as a Triple, but with significant names
 */
data class CallbackChat(val callback: CallbackHolder, val user: Long, val chatInstance: String)
