package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandsHandler
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.services.tracking.AmazonService
import com.miche.krak.kBot.services.tracking.UnieuroService
import com.miche.krak.kBot.utils.getSimpleListKeyboard
import com.miche.krak.kBot.utils.sendKeyboard
import com.miche.krak.kBot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

private const val TAG = "TRACK_COMMAND"

/**
 * This is only for testing purposes and must not be used for other reasons.
 * Use the API if you need a tracking command.
 */
class TrackCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/track",
        description = "Track",
        targets = listOf(Pair(Target.USER, Status.DEV)),
        exe = this
    )

    val acceptedStores = listOf(AmazonService(), UnieuroService())

    /**
     * First part: ask store
     */
    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        sendKeyboard(absSender, chat, "Choose the store.", getSimpleListKeyboard(acceptedStores.map { it.getName() }.toList()))
        MultiCommandsHandler.insertCommand(user, chat, ManageStore())
    }

    /**
     * Second part: check store and start service
     */
    private inner class ManageStore : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            val store = acceptedStores.find { it.getName() == arguments }
            val storeId = if (store == null) -1 else acceptedStores.indexOf(store)
            if (storeId < 0) {
                simpleMessage(absSender, "Invalid store. Retry.", chat)
                MultiCommandsHandler.insertCommand(user, chat, ManageStore())
            } else {
                store?.startTracking(absSender, user, chat)
            }
        }
    }
}