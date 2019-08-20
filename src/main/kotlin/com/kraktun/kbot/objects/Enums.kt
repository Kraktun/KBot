package com.kraktun.kbot.objects

/**
 * Chat
 * Note that invalid is used where the bot can not manage commands yet
 */
enum class Target {
    INVALID, USER, GROUP, SUPERGROUP
}

/**
 * Users status
 * Note that NOT_REGISTERED, DEV and CREATOR are usually used in private chats
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