package com.kraktun.kbot.commands

import com.kraktun.kbot.commands.core.BaseCommand
import com.kraktun.kbot.commands.core.CommandInterface
import com.kraktun.kbot.commands.core.MultiCommandInterface
import com.kraktun.kbot.commands.core.MultiCommandsHandler
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import com.kraktun.kbot.utils.executeScript
import com.kraktun.kbot.utils.getMainFolder
import com.kraktun.kbot.utils.printlnK
import com.kraktun.kbot.utils.simpleMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.objects.Message
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * YTCommand command.
 * Download audio from passed link using youtube-dl.
 * Only m4a best audio is downloaded.
 */
private const val TAG = "YTCOMMAND"

class YTCommand : CommandInterface, MultiCommandInterface {

    val engine = BaseCommand(
        command = "/yt",
        description = "Download audio from passed link using youtube-dl",
        targets = listOf(Pair(Target.USER, Status.DEV)),
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        MultiCommandsHandler.insertCommand(absSender = absSender, user = user.id, chat = chat.id, command = this)
        simpleMessage(absSender, "Send the link", chat)
    }

    override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
        GlobalScope.launch {
            val audioExtension = "m4a"
            // Remove url of video from filename
            printlnK(TAG, "Retrieving filename")
            val audio = (File(getMainFolder() + "/downloads").executeScript(
                "youtube-dl",
                "--get-filename",
                "-x",
                arguments
            )).substringBeforeLast('.').plus(".$audioExtension")
            printlnK(TAG, "Filename is $audio")
            simpleMessage(absSender, "Downloading file: $audio", chat)
            val result = File(getMainFolder() + "/downloads").executeScript(
                30,
                TimeUnit.SECONDS,
                "youtube-dl",
                "-f",
                "bestaudio[ext=$audioExtension]",
                arguments
            )
            simpleMessage(absSender, "Uploading file...", chat)
            val file = File(getMainFolder() + "/downloads/" + audio)
            printlnK(TAG, "Uploading file. Size is ${file.length()}")
            if (file.length() > 0) {
                val document = SendDocument()
                document.setChatId(chat.id)
                    .setDocument(file)
                try {
                    absSender.execute(document)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                } finally {
                    file.delete()
                }
            } else {
                simpleMessage(absSender, "An error occurred: \n$result\n\n${file.absolutePath}", chat)
                printlnK(TAG, "Error: $result\n${file.absolutePath}")
                file.delete()
            }
        }
    }
}