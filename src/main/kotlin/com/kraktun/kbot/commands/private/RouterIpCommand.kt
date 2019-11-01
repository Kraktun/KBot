package com.kraktun.kbot.commands.private

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.*
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.api.objects.Message
import java.io.File

/**
 * Get list of client ips from dd-wrt router
 */
class RouterIpCommand : CommandInterface {

    val engine = BaseCommand(
        command = "/ips",
        description = "Get router clients",
        targets = listOf(Pair(Target.USER, Status.CREATOR)),
        exe = this
    )

    override fun execute(absSender: AbsSender, message: Message) {
        simpleMessage(absSender, "Getting IPs", message.chat)
        // Too many splits and maps but there are only a few entries, so...
        val ips = File(getMainFolder()).executeScript(
            "/bin/sh",
            "-c",
            // Path to a simple bash script with a single command: "arp -a"
            "cat /home/capo/scripts/get_router_ip.sh | ssh root@192.168.1.1"
        ).split("\n").map {
            it.split(" ").subList(0, 2) // sublist because I need only name and ip, not mac or interface
        }.map {
            "${it[0]} ${it[1]}\n"
        }.reduce { acc, s -> "$acc $s" }
        simpleHTMLMessage(absSender = absSender, s = "<b>IPS:</b>\n$ips", c = message.chat)
    }
}