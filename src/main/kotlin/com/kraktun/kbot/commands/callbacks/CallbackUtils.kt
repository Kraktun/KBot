package com.kraktun.kbot.commands.callbacks

import com.kraktun.kbot.commands.callbacks.exceptions.MessageTooLongInCallbackResult

const val MAX_CALLBACK_MESSAGE_LENGTH = 200

fun checkMessageLength(message: String): Boolean {
    if (message.length > MAX_CALLBACK_MESSAGE_LENGTH) {
        throw MessageTooLongInCallbackResult()
    }
    return true
}
