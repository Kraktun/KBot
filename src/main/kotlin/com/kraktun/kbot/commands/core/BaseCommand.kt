package com.kraktun.kbot.commands.core

import com.kraktun.kbot.data.Configurator
import com.kraktun.kbot.data.InvalidDataManagerException
import com.kraktun.kbot.objects.GroupStatus
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.ChatMember
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

/**
 * Class that represents a command.
 * Single commands must extend this class.
 */
class BaseCommand(
    // string that fires the command, starting symbol may be anything (e.g. '/', '#'). Must be unique for a single bot.
    val command: String,
    // description for the command, used when calling /help. Supports html.
    val description: String = "",
    // List of pairs<chat, status>.
    // chat is the type of chat where the message was sent
    // status is the minimum status the user who sends the command must have to fire a reply
    // Status is different between groups and user chats
    // Here status depends on the target: if chat is group => status = groupStatus, else is the userStatus
    val targets: Map<Target, Status>,
    // number of arguments after the command (in the same message) necessary to consider the command correct
    private val argsNum: Int = 0,
    // function with additional logic to execute before firing the command
    // only non-intensive (aka non-DB) operations should be done here
    private val filterFun: (message: Message) -> Boolean = { true },
    // define other options necessary for this command e.g. BOT_IS_ADMIN
    private val chatOptions: List<ChatOption> = mutableListOf(),
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
        if (Configurator.dataManager[absSender.username()] == null)
            throw InvalidDataManagerException("DataManger for bot: ${absSender.username()} does not exist")
        // apply filters
        val result = filterAll(absSender, message)
        runBlocking {
            GlobalScope.launch {
                if (result == FilterResult.FILTER_RESULT_OK)
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
            !filterFun(message) -> FilterResult.INVALID_PRECONDITIONS
            !filterChat(chat) -> FilterResult.INVALID_CHAT
            !filterStatus(absSender, user, chat) -> FilterResult.INVALID_STATUS
            !filterFormat(arguments) -> FilterResult.INVALID_FORMAT
            !filterChatOptions(absSender, chat) -> FilterResult.INVALID_CHAT_OPTIONS
            else -> FilterResult.FILTER_RESULT_OK
            // filterBans is not necessary as  someone may decide to enable a command for banned users
            // use filterStatus for that
        }
    }

    /**
     * Return true if message received comes from a valid chat.
     * In other words if the chat is part of the targets list.
     * Used only for better handling of errors.
     */
    private fun filterChat(chat: Chat): Boolean {
        return targets[chat.toEnum()] != null
    }

    /**
     * Return true if message received comes from a valid chat and user.
     * In other words if the chat is part of the targets list and the status of the user is equal to or higher than the privacy.
     * Check on group status is done in filterBannedGroup().
     */
    private fun filterStatus(absSender: AbsSender, user: User?, chat: Chat): Boolean {
        if (chat.isChannelChat) return true // no status in chats
        val userStatus: Status = Configurator.dataManager[absSender.username()]!!.getUserStatus(user, chat)
        val r = targets[chat.toEnum()]
        return if (r != null) r <= userStatus else false
    }

    /**
     * Return true if command is formatted correctly.
     */
    private fun filterFormat(arguments: List<String>): Boolean {
        // in the future will manage patterns (use pattern?.let{})
        return arguments.size >= argsNum
    }

    /**
     * Return true if chat options are satisfied.
     */
    private fun filterChatOptions(absSender: AbsSender, chat: Chat): Boolean {
        var result = if (ChatOption.BOT_IS_ADMIN in chatOptions) filterBotAdmin(absSender, chat) else true
        result = result && if (ChatOption.ALLOW_BANNED_GROUPS !in chatOptions) filterBannedGroup(absSender, chat) else true
        return result
    }

    /**
     * Return true if command does not need bot as admin or if bot is admin.
     */
    private fun filterBotAdmin(absSender: AbsSender, chat: Chat): Boolean {
        if (chat.isChannelChat) return true // All bots are admins in channels
        val botId = absSender.botToken().substringBefore(":").toInt()
        return if (chatOptions.contains(ChatOption.BOT_IS_ADMIN) && chat.isGroupOrSuper()) {
            val getAdmins = GetChatAdministrators()
            getAdmins.chatId = chat.id.toString()
            val admins = executeMethod(absSender, getAdmins) ?: listOf<ChatMember>()
            admins.any {
                it.user.id == botId
            }
        } else true
    }

    /**
     * Return true if group is not banned.
     */
    private fun filterBannedGroup(absSender: AbsSender, chat: Chat): Boolean {
        return if (chat.isGroupOrSuper()) {
            Configurator.dataManager[absSender.username()]!!.getGroupStatus(chat.id) != GroupStatus.BANNED
        } else true
    }
}
