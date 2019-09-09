package com.kraktun.kbot

import com.kraktun.kbot.objects.UserK

/*
To start the bot:
1) Change following values to match your id (you need only one token + name)
2) In 'MainBot.kt' change 'getBotUsername()' and 'getBotToken()' to match your chosen values
*/

const val MAIN_TOKEN = "000000000:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
const val MAIN_NAME = "my_bot"

const val TEST_TOKEN = "000000000:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
const val TEST_NAME = "my_bot_test"

const val PING_BOT_TOKEN = "000000000:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
const val PING_BOT_NAME = "my_ping_bot"
const val PING_BOT_ALERT = 0L
const val PING_PONG_CHAT = 0L

const val PING_RECEIVER_TOKEN = "000000000:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
const val PING_RECEIVER_NAME = "my_pong_bot"

val predefinedUsers = listOf<UserK>()
