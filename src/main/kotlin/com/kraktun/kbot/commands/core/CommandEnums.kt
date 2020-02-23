package com.kraktun.kbot.commands.core

enum class FilterResult {

    FILTER_RESULT_OK,
    INVALID_CHAT,
    INVALID_STATUS,
    INVALID_FORMAT,
    INVALID_PRECONDITIONS, // used for filterFun
    INVALID_CHAT_OPTIONS,
    NOT_COMMAND
}

enum class ChatOption {

    BOT_IS_ADMIN, // Bot must be admin in the group
    ALLOW_BANNED_GROUPS // Allow a command to be used in banned groups
}
