package com.kraktun.kbot.commands.callbacks.exceptions

import com.kraktun.kbot.commands.callbacks.MAX_CALLBACK_MESSAGE_LENGTH

class MessageTooLongInCallbackResult(message: String? = "Message is too long. Max number of characters allowed is $MAX_CALLBACK_MESSAGE_LENGTH") : Exception(message)
