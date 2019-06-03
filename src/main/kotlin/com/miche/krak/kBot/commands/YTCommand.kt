package com.miche.krak.kBot.commands

import com.miche.krak.kBot.commands.core.BaseCommand
import com.miche.krak.kBot.commands.core.CommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandInterface
import com.miche.krak.kBot.commands.core.MultiCommandsHandler
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.objects.Target
import com.miche.krak.kBot.utils.executeScript
import com.miche.krak.kBot.utils.getMainFolder
import com.miche.krak.kBot.utils.printlnK
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
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
        argsNum = 0,
        exe = this
    )

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: List<String>, message: Message) {
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        answer.text = "Send the link"
        MultiCommandsHandler.insertCommand(userId = user.id, chatId = chat.id, command = this)
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    override fun executeAfter(absSender: AbsSender, user: User, chat: Chat, arguments: String, message: Message, data: Any?) {
        val audioExtension = "m4a"
        val answer = SendMessage()
        answer.chatId = chat.id.toString()
        //Remove url of video from filename
        printlnK(TAG, "Retrieving filename")
        val audio = (File(getMainFolder() + "/downloads").executeScript("youtube-dl", "--get-filename", "-x", arguments)).substringBeforeLast('.').plus(".$audioExtension")
        printlnK(TAG, "Filename is $audio")
        answer.text = "Downloading file: $audio"
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
        val result = File(getMainFolder() + "/downloads").executeScript(30, TimeUnit.SECONDS, "youtube-dl", "-f", "bestaudio[ext=$audioExtension]", arguments)
        answer.text = "Uploading file..."
        try {
            absSender.execute(answer)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
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
            try {
                printlnK(TAG, "Error: $result\n${file.absolutePath}")
                answer.text = "An error occurred: \n$result\n\n${file.absolutePath}"
                absSender.execute(answer)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            } finally {
                file.delete()
            }
        }
    }
}