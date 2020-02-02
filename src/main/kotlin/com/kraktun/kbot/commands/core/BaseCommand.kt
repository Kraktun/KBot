package com.kraktun.kbot.commands.core

import com.kraktun.kbot.objects.GroupStatus
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.commands.core.FilterResult.*
import com.kraktun.kbot.commands.core.ChatOptions.*
import com.kraktun.kbot.data.Configurator
import com.kraktun.kbot.utils.*
import com.kraktun.kutils.collections.ifNotEmpty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    private val filterFun: (message: Message) -> Boolean = { true },
    // define other options necessary fot this command
    private val chatOptions: List<ChatOptions> = mutableListOf(),
    // Function to execute when a filter (including filterFun) fails and returns false
    private val onError: (absSender: AbsSender, message: Message, filterResult: FilterResult) -> Unit = { _, _, _ -> },
    // implementation of the CommandInterface (aka execute method)
    private val exe: CommandInterface
) {

    /**
     * Fire execute command of CommandInterface if all filters pass.
     * Return result of filters.
     */
    fun fire(absSender: AbsSender, message: Message): FilterResult {
        // apply filters
        val result = filterAll(absSender, message)
        runBlocking {
            GlobalScope.launch {
                if (result == FILTER_RESULT_OK)
                    exe.execute(absSender, message)
                else
                    onError(absSender, message, result)
            }
        }
        return result
    }

    /**
     * Apply all filters. Return true if everything is ok.
     */
    private fun filterAll(absSender: AbsSender, message: Message): FilterResult {
        val user = message.from
        val chat = message.chat
        val arguments = message.arguments()
        return when {
            !filterFun(message) -> INVALID_PRECONDITIONS
            !filterChat(chat) -> INVALID_CHAT
            !filterStatus(user, chat) -> INVALID_STATUS
            !filterFormat(arguments) -> INVALID_FORMAT
            !filterLock(user, chat) -> LOCKED_CHAT
            !filterBotAdmin(absSender, chat) -> BOT_NOT_ADMIN
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
            it.first == chat.toEnum()
        }.toList().isNotEmpty()
    }

    /**
     * Return true if message received comes from a valid chat and user.
     * In other words if the chat is part of the targets list and the status of the user is equal to or higher than the privacy.
     */
    private fun filterStatus(user: User?, chat: Chat): Boolean {
        if (chat.isChannelChat) return true
        val userStatus: Status = getDBStatus(user, chat)
        return targets.filter {
            it.first == chat.toEnum()
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
        if (chat.isChannelChat) return true // All bots are admins in channels
        val botId = absSender.botToken().substringBefore(":").toInt()
        return if (chatOptions.contains(BOT_IS_ADMIN) && chat.isGroupOrSuper()) {
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

    companion object {

        /**
         * Filter used for locked groups.
         * Return true if message is allowed (aka group not locked or status >= admin).
         */
        fun filterLock(user: User?, chat: Chat): Boolean {
            return !chat.isGroupOrSuper() ||
                    Configurator.dataManager.getGroupStatus(chat.id) != GroupStatus.LOCKED ||
                    Configurator.dataManager.getGroupUserStatus(chat.id, user!!.id) >= Status.ADMIN
        }

        /**
         * Filter used for banned users.
         * Return true if message is allowed (aka user not banned).
         */
        fun filterBans(user: User, chat: Chat): Boolean {
            return chat.isChannelChat || (
                    Configurator.dataManager.getUser(user.id)?.status != Status.BANNED &&
                            Configurator.dataManager.getGroupUserStatus(chat.id, user.id) != Status.BANNED)
        }
    }
}