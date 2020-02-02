package com.kraktun.kbot.commands.core

enum class FilterResult {

    FILTER_RESULT_OK,
    INVALID_CHAT,
    INVALID_STATUS,
    INVALID_FORMAT,
    INVALID_PRECONDITIONS, // used for filterFun
    LOCKED_CHAT,
    BANNED_USER,
    BOT_NOT_ADMIN,
    NOT_COMMAND
}

enum class ChatOptions {

    BOT_IS_ADMIN // Bot must be admin
}