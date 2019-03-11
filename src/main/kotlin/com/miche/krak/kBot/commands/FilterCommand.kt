package com.miche.krak.kBot.commands

import com.miche.krak.kBot.utils.Privacy
import com.miche.krak.kBot.utils.Target
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User

class FilterCommand(private val targets : List<Target> = listOf(Target.USER, Target.GROUP, Target.CHANNEL),
                    private val privacy : Privacy = Privacy.USER,
                    private val argsNum : Int = 0) {

    fun filterFrom(user : User, chat : Chat) : Boolean {
        val userStatus = Privacy.ADMIN //TODO CHECK FROM DB
        if (userStatus.ordinal < privacy.ordinal)
            return false
        val chatValue = if (chat.isGroupChat || chat.isSuperGroupChat) { Target.GROUP }
            else if (chat.isChannelChat) {Target.CHANNEL } else {Target.USER}
        if (!targets.contains(chatValue))
            return false
        return true
    }

    fun filterFormat(arguments : List<String>) : Boolean {
        return arguments.size >= argsNum
    }


}