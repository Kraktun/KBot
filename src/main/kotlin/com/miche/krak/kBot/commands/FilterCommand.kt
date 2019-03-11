package com.miche.krak.kBot.commands

import com.miche.krak.kBot.utils.Privacy
import com.miche.krak.kBot.utils.Target
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import java.util.regex.Pattern

/**
 * Apply filters to messages received, regarding target chat, user permissions and arguments
 */
class FilterCommand(private val targets : List<Target> = listOf(Target.USER, Target.GROUP, Target.CHANNEL),
                    private val privacy : Privacy = Privacy.USER,
                    private val argsNum : Int = 0,
                    private val argsPattern : Pattern? = null) {

    /**
     * Return true if message received comes from a valid chat
     */
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

    /**
     * Return true if command is formatted correctly
     */
    fun filterFormat(arguments : List<String>) : Boolean {
        if (argsPattern != null) {
            //MANAGE PATTERN
            return true
        } else
            return arguments.size >= argsNum
    }

    /**
     * Apply all filters. Return true if everything is ok
     */
    fun filterAll(user : User, chat : Chat, arguments : List<String>) : Boolean {
        return filterFrom(user, chat) && filterFormat(arguments)
    }

}