package com.miche.krak.kBot.commands.core

import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.objects.GroupStatus
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.chatMapper
import com.miche.krak.kBot.utils.getDBStatus
import com.miche.krak.kBot.utils.ifNotEmpty
import com.miche.krak.kBot.commands.core.FilterResult.*
import com.miche.krak.kBot.commands.core.ChatOptions.*
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

/**
 * Class that represents a command.
 * Single commands must extend this class.
 */
class BaseCommand(
    // string that fires the command, starting symbol may be anything (e.g. '/', '#'). Must be unique.
    val command: String,
    // description for the command, used when calling /help
    val description: String = "",
    // List of pairs<chat, status>.
    // chat is the type of chat where the message was sent
    // status is the minimum status the user who sent the command must have to fire a reply
    // Status is different between groups and user chats
    // Here status depends on the target: if chat is group => status = groupStatus, else is the userStatus (from DB)
    val targets: List<Pair<Target, Status>>,
    // number of arguments after the command (same message) necessary to process the command
    private val argsNum: Int = 0,
    // function with additional logic to execute before firing the command
    // only non-intensive (aka non-DB) operations should be done here
    private val filterFun: (Message) -> Boolean = { true },
    // define other options necessary fot this command
    private val chatOptions: List<ChatOptions> = mutableListOf(),
    // Function to execute when a filter (including filterFun) fails and returns false
    private val onError: (AbsSender, List<String>, Message, FilterResult) -> Unit = { _, _, _, _ -> },
    // implementation of the CommandInterface (aka execute method)
    private val exe: CommandInterface
) {

    /**
     * Fire execute command of CommandInterface if all filters pass.
     * Return result of filters.
     */
    fun fire(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message): FilterResult {
        // apply filters
        val result = filterAll(absSender, arguments, message)
        if (result == FILTER_RESULT_OK)
            exe.execute(absSender, user, chat, arguments, message)
        else
            onError(absSender, arguments, message, result)
        return result
    }

    /**
     * Apply all filters. Return true if everything is ok.
     */
    private fun filterAll(absSender: AbsSender, arguments: List<String>, message: Message): FilterResult {
        val user = message.from
        val chat = message.chat
        return when {
            !filterFun(message) -> INVALID_PRECONDITIONS
            !filterChat(chat) -> INVALID_CHAT
            !filterStatus(user, chat) -> INVALID_STATUS
            !filterFormat(arguments) -> INVALID_FORMAT
            !filterLock(user, chat) -> LOCKED_CHAT
            !filterBotAdmin(absSender, chat) -> BOT_NOT_ADMIN
            !filterAllUserAdmin(chat) -> ALL_USER_ADMINS_ENABLED
            else -> FILTER_RESULT_OK
            // filterBans is not necessary as this check is already performed by filterChat()
            // (and someone may decide to enable a command for banned users)
        }
    }

    /**
     * Return true if message received comes from a valid chat.
     * In other words if the chat is part of the targets list.
     * Used only for better handling of errors.
     */
    private fun filterChat(chat: Chat): Boolean {
        return targets.filter {
            it.first == chatMapper(chat)
        }.toList().isNotEmpty()
    }

    /**
     * Return true if message received comes from a valid chat and user.
     * In other words if the chat is part of the targets list and the status of the user is equal to or higher than the privacy.
     */
    private fun filterStatus(user: User, chat: Chat): Boolean {
        val userStatus: Status = getDBStatus(user, chat)
        return targets.filter {
            it.first == chatMapper(chat)
        }.ifNotEmpty({
            this[0].second <= userStatus // [0] as a command can have only one single pair with a unique Target
        }, default = false) as Boolean
    }

    /**
     * Return true if command is formatted correctly.
     */
    private fun filterFormat(arguments: List<String>): Boolean {
        // in the future will manage patterns (use pattern?.let{})
        return arguments.size >= argsNum
    }

    /**
     * Return true if command does not need bot as admin or if bot is admin
     */
    private fun filterBotAdmin(absSender: AbsSender, chat: Chat): Boolean {
        val botId = (absSender as TelegramLongPollingBot).botToken.substringBefore(":").toInt()
        return if (chatOptions.contains(BOT_IS_ADMIN) && (chat.isGroupChat || chat.isSuperGroupChat)) {
            val getAdmins = GetChatAdministrators()
            getAdmins.chatId = chat.id.toString()
            try {
                val admins = absSender.execute(getAdmins)
                admins.any {
                    it.user.id == botId
                }
            } catch (e: TelegramApiException) {
                e.printStackTrace()
                false
            }
        } else true
    }

    /**
     * Return true if the command does not need the option allUserAreAdmins to be disabled
     * or if option allUserAreAdmins is not enabled
     */
    private fun filterAllUserAdmin(chat: Chat): Boolean {
        return !(chatOptions.contains(OPTION_ALL_USER_ADMIN_DISABLED) &&
                chat.isGroupChat &&
                chat.allMembersAreAdministrators)
    }

    companion object {

        /**
         * Filter used for locked groups.
         * Return true if message is allowed (aka group not locked or status >= admin).
         */
        fun filterLock(user: User, chat: Chat): Boolean {
            return !(chat.isGroupChat || chat.isSuperGroupChat) ||
                    DatabaseManager.getGroupStatus(chat.id) != GroupStatus.LOCKED ||
                        DatabaseManager.getGroupUserStatus(chat.id, user.id) >= Status.ADMIN
        }

        /**
         * Filter used for banned users.
         * Return true if message is allowed (aka user not banned).
         */
        fun filterBans(user: User, chat: Chat): Boolean {
            return DatabaseManager.getUser(user.id)?.status != Status.BANNED &&
                    DatabaseManager.getGroupUserStatus(chat.id, user.id) != Status.BANNED
        }
    }
}