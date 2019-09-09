package com.kraktun.kbot.commands.functions

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.commands.core.MultiCommandInterface
import com.kraktun.kbot.commands.core.MultiCommandsHandler
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.services.tracking.AmazonService
import com.kraktun.kbot.services.tracking.UnieuroService
import com.kraktun.kbot.utils.getSimpleListKeyboard
import com.kraktun.kbot.utils.sendKeyboard
import com.kraktun.kbot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.objects.Message
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
    override fun execute(absSender: AbsSender, message: Message) {
        sendKeyboard(absSender, message.chat, "Choose the store.", getSimpleListKeyboard(acceptedStores.map { it.getName() }.toList()))
        MultiCommandsHandler.insertCommand(absSender, message.from, message.chat, ManageStore())
    }

    /**
     * Second part: check store and start service
     */
    private inner class ManageStore : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, message: Message, data: Any?) {
            val store = acceptedStores.find { it.getName() == message.text }
            val storeId = if (store == null) -1 else acceptedStores.indexOf(store)
            if (storeId < 0) {
                simpleMessage(absSender, "Invalid store. Retry.", message.chat)
                MultiCommandsHandler.insertCommand(absSender, message.from, message.chat, ManageStore())
            } else {
                store?.startTracking(absSender, message.from, message.chat)
            }
        }
    }
}