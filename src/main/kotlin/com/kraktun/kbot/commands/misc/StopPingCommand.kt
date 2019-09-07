package com.kraktun.kbot.commands.misc

import com.kraktun.kbot.PING_BOT_GROUP
import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.jobs.JobExecutor
import com.kraktun.kbot.jobs.PingJob
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.simpleMessage
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * Simple command
 */
class StopPingCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/stopping",
        description = "Stop pinging",
        targets = listOf(Pair(Target.GROUP, Status.ADMIN)),
        exe = this
    )

    override fun execute(absSender: AbsSender, message: Message) {
        if (message.chatId == PING_BOT_GROUP) {
            JobExecutor.removeJob(PingJob.jobInfo) // I use add/remove rather than some check because I need only 1 pingBot
            simpleMessage(absSender = absSender, s = "Disabled ping", c = message.chat)
        }
    }
}