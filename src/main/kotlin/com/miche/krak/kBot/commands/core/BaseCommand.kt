package com.miche.krak.kBot.commands.core

import com.miche.krak.kBot.utils.Status
import com.miche.krak.kBot.utils.Target
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.util.regex.Pattern

class BaseCommand(
    val command : String,
    val description : String = "",
    private val targets : List<Target> = listOf(Target.USER, Target.GROUP, Target.CHANNEL),
    private val privacy : Status = Status.USER,
    private val argsNum : Int = 0,
    private val argsPattern : Pattern? = null,
    private val exe : CommandInterface ) {


    fun fire(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>) {
        //apply filters
        if (filterAll(user, chat, arguments))
            exe.execute(absSender, user, chat, arguments)
    }

    /**
     * Return true if message received comes from a valid chat
     */
    private fun filterFrom(user : User, chat : Chat) : Boolean {
        val userStatus = Status.ADMIN //TODO CHECK FROM DB
        if (userStatus.ordinal < privacy.ordinal)
            return false
        val chatValue = if (chat.isGroupChat || chat.isSuperGroupChat) { Target.GROUP }
        else if (chat.isChannelChat) {
            Target.CHANNEL }
        else {
            Target.USER}
        if (!targets.contains(chatValue))
            return false
        return true
    }

    /**
     * Return true if command is formatted correctly
     */
    private fun filterFormat(arguments : List<String>) : Boolean {
        //manage pattern (use pattern?.let{})
        return arguments.size >= argsNum
    }

    /**
     * Apply all filters. Return true if everything is ok
     */
    private fun filterAll(user : User, chat : Chat, arguments : List<String>) : Boolean {
        return filterFrom(user, chat) && filterFormat(arguments)
    }
}