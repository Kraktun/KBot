package com.kraktun.kbot.commands.callbacks

class MessageTooLongInCallbackResult(message: String? = "Message is too long. Max number of characters allowed is $MAX_CALLBACK_MESSAGE_LENGTH") : Exception(message)