package com.miche.krak.kBot.commands.core

import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.objects.GroupStatus
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.safeEmpty
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Class that represents a command.
 * Single commands must extend this class.
 */
class BaseCommand (
    //string that fires the command, without '/'. Must be unique.
    val command : String,
    //description for the command
    val description : String = "",
    //list of types of chat where this command is available
    val targets : List<Pair<Target, Status>>,
    //minimum status the user who sent the command must have to fire a reply
    //Status is different between gorup and user chats
    //Here status depends on the target: if chat is group => status = groupStatus, else is the userStatus (from DB)
    //number of arguments after the command necessary to process the command (same message, not multi commands)
    private val argsNum : Int = 0,
    //function with additional logic to execute before firing the command
    //only non-intensive (aka non-DB) operations should be done here
    private val filterFun : (Message) -> Boolean = {true},
    //implementation of the CommandInterface (aka execute method)
    private val exe : CommandInterface ) {

    /**
     * Fire execute command of CommandInterface if all filters pass.
     * Return result of filters.
     */
    fun fire(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) : Boolean {
        //apply filters
        return if (filterAll(user, chat, arguments, message)) {
            exe.execute(absSender, user, chat, arguments, message)
            true
        } else false
    }

    /**
     * Apply all filters. Return true if everything is ok.
     */
    private fun filterAll(user : User, chat : Chat, arguments : List<String>, message: Message) : Boolean {
        return filterFun(message) && filterFrom(user, chat) &&
                filterFormat(arguments) &&
                filterLock(user, chat)
                //filterBans is not necessary as this check is already performed by filterFrom()
                // (and someone may decide to enable a command for banned users)
    }

    /**
     * Return true if message received comes from a valid chat and a valid user.
     * IN other words if the chat is part of the targets list and the status of the user is equal to or higher than the privacy.
     */
    private fun filterFrom(user : User, chat : Chat) : Boolean {
        var userStatus : Status = Status.NOT_REGISTERED
        val chatValue = if (chat.isGroupChat || chat.isSuperGroupChat) {
            userStatus = DatabaseManager.getGroupUserStatus(chat.id, user.id)
            Target.GROUP
        } else if (chat.isUserChat) {
            userStatus = DatabaseManager.getUser(user.id)?.status ?: Status.NOT_REGISTERED
            Target.USER}
        else
            Target.INVALID
        return targets.filter {
            it.first == chatValue
        }.safeEmpty({
            this[0].second <= userStatus //[0] as a command can have only one single pair with a unique Target
        }, false) as Boolean
    }

    /**
     * Return true if command is formatted correctly.
     */
    private fun filterFormat(arguments : List<String>) : Boolean {
        //manage pattern (use pattern?.let{})
        return arguments.size >= argsNum
    }

    companion object {

        /**
         * Filter used for locked groups.
         * Return true if message is allowed.
         */
        fun filterLock(user: User, chat: Chat): Boolean {
            return !(chat.isGroupChat || chat.isSuperGroupChat) ||
                    DatabaseManager.getGroupStatus(chat.id) != GroupStatus.LOCKED ||
                        DatabaseManager.getGroupUserStatus(chat.id, user.id) >= Status.ADMIN
        }

        /**
         * Filter used for banned users.
         * Return true if message is allowed.
         */
        fun filterBans(user: User, chat: Chat): Boolean {
            return DatabaseManager.getUser(user.id)?.status != Status.BANNED &&
                    DatabaseManager.getGroupUserStatus(chat.id, user.id) != Status.BANNED
        }
    }
}