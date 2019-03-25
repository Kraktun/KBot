package com.miche.krak.kBot.utils

/**
 * Chat
 * Note that invalid is used where the bot can not manage commands yet
 */
enum class Target {
    INVALID, USER, GROUP
}

/**
 * Users status
 * Note that NOT_REGISTERED, DEV and CREATOR can only be used in private chats
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