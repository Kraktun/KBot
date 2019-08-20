package com.kraktun.kbot.commands

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.simpleMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

class TestCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/test",
        description = "Command to test new commands",
        targets = listOf(Pair(Target.USER, Status.ADMIN)),
        exe = this
    )

    override fun execute(absSender: AbsSender, message: Message) {
        Thread.sleep(5000)
        simpleMessage(absSender, "TEST COMPLETED", message.chat)
    }
}