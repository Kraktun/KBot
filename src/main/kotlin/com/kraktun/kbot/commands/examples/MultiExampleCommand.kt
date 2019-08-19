package com.kraktun.kbot.commands.examples

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.commands.core.MultiCommandInterface
import com.kraktun.kbot.commands.core.MultiCommandsHandler
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * Example for a multiple ask-answer command.
 */
class MultiExampleCommand : CommandInterface, MultiCommandInterface {

    val engine = BaseCommand(
        command = "/multi",
        description = "Multi",
        targets = listOf(Pair(Target.USER, Status.DEV)),
        exe = this
    )

    /**
     * Executed when command is first sent.
     */
    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        MultiCommandsHandler.insertCommand(absSender = absSender, user = user.id, chat = chat.id, command = this)
        simpleMessage(absSender, "First part", chat)
    }

    /**
     * Executed when first reply is received.
     * @arguments is the reply to what is sent above.
     * @data is usually empty here
     */
    override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
        MultiCommandsHandler.insertCommand(absSender = absSender, user = user.id, chat = chat.id, command = ThirdStep(), data = arguments)
        simpleMessage(absSender, "Second part", chat)
    }

    /**
     * Executed after the second reply.
     * @arguments is the new reply.
     * @data is the old reply.
     */
    class ThirdStep : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            MultiCommandsHandler.deleteCommand(absSender = absSender, user = user.id, chat = chat.id)
            simpleMessage(absSender, "Third part, data is $data, \nnew data is $arguments", chat)
        }
    }
}