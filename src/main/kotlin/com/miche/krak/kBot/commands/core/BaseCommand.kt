package com.miche.krak.kBot.commands.core

import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.utils.GroupStatus
import com.miche.krak.kBot.utils.Status
import com.miche.krak.kBot.utils.Target
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.util.regex.Pattern

class BaseCommand(
    val command : String,
    val description : String = "",
    private val targets : List<Target>,
    //Status is independent from the target in DB: private status may be != group status
    //Here status depends on the target: if chat is group => status = groupStatus
    private val privacy : Status,
    private val argsNum : Int = 0,
    private val argsPattern : Pattern? = null, //TODO
    private val filterFun : (Message) -> Boolean = {true}, //function with additional logic to execute before firing the command
        //only non-intensive (aka non-DB) operations should be done here
    private val exe : CommandInterface ) {

    /**
     * Fire executable with passed values
     */
    fun fire(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) : Boolean {
        //apply filters
        return if (filterAll(user, chat, arguments, message)) {
            exe.execute(absSender, user, chat, arguments, message)
            true
        } else false
    }

    /**
     * Apply all filters. Return true if everything is ok
     */
    private fun filterAll(user : User, chat : Chat, arguments : List<String>, message: Message) : Boolean {
        return filterFun(message) && filterFrom(user, chat) &&
                filterFormat(arguments) &&
                filterLock(user, chat) &&
                filterStatus(user, chat)
    }

    /**
     * Return true if message received comes from a valid chat and a valid user
     */
    private fun filterFrom(user : User, chat : Chat) : Boolean {
        var userStatus : Status = Status.NOT_REGISTERED
        val chatValue = if (chat.isGroupChat || chat.isSuperGroupChat) {
            userStatus = DatabaseManager.instance.getGroupUserStatus(chat.id, user.id)
            Target.GROUP
        } else if (chat.isUserChat) {
            userStatus = DatabaseManager.instance.getUser(user.id)?.status ?: Status.NOT_REGISTERED
            Target.USER}
        else
            Target.INVALID
        return (userStatus >= privacy && targets.contains(chatValue))
    }

    /**
     * Return true if command is formatted correctly
     */
    private fun filterFormat(arguments : List<String>) : Boolean {
        //manage pattern (use pattern?.let{})
        return arguments.size >= argsNum
    }

    companion object {

        /**
         * Filter used for locked groups.
         * True if message is allowed.
         */
        fun filterLock(user: User, chat: Chat): Boolean {
            return !(chat.isGroupChat || chat.isSuperGroupChat) || (
                    DatabaseManager.instance.getGroupStatus(chat.id) != GroupStatus.LOCKED ||
                        DatabaseManager.instance.getGroupUserStatus(chat.id, user.id) >= Status.ADMIN)
        }

        /**
         * Filter used for banned users
         * True if message is allowed.
         */
        fun filterStatus(user: User, chat: Chat): Boolean {
            return DatabaseManager.instance.getUser(user.id)?.status != Status.BANNED &&
                    DatabaseManager.instance.getGroupUserStatus(chat.id, user.id) != Status.BANNED
        }
    }
}