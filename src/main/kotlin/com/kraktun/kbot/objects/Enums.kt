package com.kraktun.kbot.objects

/**
 * Chat
 * Note that invalid is used where the bot can not manage commands yet
 */
enum class Target {
    INVALID, USER, GROUP, SUPERGROUP, CHANNEL
}

/**
 * Users status
 * Note that NOT_REGISTERED, DEV and CREATOR can only be used in USER chats
 * CHANNELS ignore any status and always use NOT_REGISTERED
 */
enum class Status {
    BANNED, NOT_REGISTERED, USER, POWER_USER, ADMIN, DEV, CREATOR
}

/**
 * Group status
 */
enum class GroupStatus {
    NORMAL, LOCKED
}
