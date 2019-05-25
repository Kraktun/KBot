package com.miche.krak.kBot

import com.miche.krak.kBot.objects.UserK
/*
To start the bot:
1) Rename this file 'BotConfig.kt'
2) Remove the object 'Template' (not its values)
3) Change following values to match your id (you need only one token + name)
4) In 'MainBot.kt' change 'getBotUsername()' and 'getBotToken()' to match your chosen values
 */
object Template {
    const val MAIN_TOKEN = "000000000:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    const val MAIN_NAME = "my_bot"

    const val TEST_TOKEN = "000000000:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    const val TEST_NAME = "my_test_bot"

    val predefinedUsers = listOf<UserK>( )
}