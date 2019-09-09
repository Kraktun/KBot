package com.kraktun.kbot.utils

import com.kraktun.kbot.database.DatabaseManager
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.meta.generics.WebhookBot

/**
 * Returns status of user according to the passed chat
 */
fun getDBStatus(user: User?, chat: Chat): Status {
    return when {
        user == null -> Status.NOT_REGISTERED
        chat.isUserChat -> DatabaseManager.getUser(user.id)?.status ?: Status.NOT_REGISTERED
        chat.isGroupOrSuper() -> DatabaseManager.getGroupUserStatus(groupId = chat.id, userId = user.id)
        else -> Status.NOT_REGISTERED
    }
}

/**
 * Get identification string for user:
 * if it has a username, use that, if not use first + last name or only first
 */
fun User.getFormattedName(): String {
    return when {
        userName != null -> "@$userName"
        lastName != null -> "$firstName $lastName"
        else -> firstName
    }
}

/**
 * Get username of bot
 */
fun AbsSender.username(): String {
    return if (this is LongPollingBot) {
        this.botUsername
    } else {
        (this as WebhookBot).botUsername
    }
}

/**
 * Get token of bot
 */
fun AbsSender.botToken(): String {
    return if (this is LongPollingBot) {
        this.botToken
    } else {
        (this as WebhookBot).botToken
    }
}

/**
 * Convenient way to check if it's a group or supergroup message
 */
fun Message.isGroupOrSuper(): Boolean {
    return this.isGroupMessage || this.isSuperGroupMessage
}

/**
 * Get arguments of message.
 */
fun Message.arguments(): List<String> {
    return this.text.substringAfter(" ") // take args from second word (first is the command)
        .split(" ") // put each word in the list
}

/**
 * Convenient way to check if it's a group or supergroup chat
 */
fun Chat.isGroupOrSuper(): Boolean {
    return this.isSuperGroupChat || this.isGroupChat
}

/**
 * Maps a chat into the corresponding enum
 */
fun Chat.toEnum(): Target {
    return when {
        isGroupChat -> Target.GROUP
        isSuperGroupChat -> Target.SUPERGROUP
        isUserChat -> Target.USER
        isChannelChat -> Target.CHANNEL
        else -> Target.INVALID
    }
}