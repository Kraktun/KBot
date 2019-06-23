package com.miche.krak.kBot.commands.examples

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandsHandler
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.removeKeyboard
import com.miche.krak.kBot.utils.sendKeyboard
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.bots.AbsSender

class KeyboardExampleCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/key",
        description = "Keyboard",
        targets = listOf(Pair(Target.USER, Status.DEV)),
        exe = this
    )
    private val buttons = listOf("option1", "option2")

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        val row = KeyboardRow()
        buttons.forEach { row.add(it) }
        val key = ReplyKeyboardMarkup()
        key.keyboard.addAll(listOf(row))
        key.resizeKeyboard = true
        MultiCommandsHandler.insertCommand(user = user, chat = chat, command = CommandChosen())
        sendKeyboard(absSender = absSender, c = chat, s = "KEY", keyboard = key)
    }

    class CommandChosen : MultiCommandInterface {
        override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
            removeKeyboard(absSender, chat, "You chose ${message.text}")
        }
    }
}