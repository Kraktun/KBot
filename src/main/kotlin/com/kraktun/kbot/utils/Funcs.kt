package com.kraktun.kbot.utils

import com.kraktun.kbot.database.DatabaseManager
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.Target
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.meta.generics.WebhookBot
import java.io.File
import java.net.URLDecoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Get identification string for user:
 * if it has a username, use that, if not use first + last name or only first
 */
fun getQualifiedUser(user: User): String {
    return when {
        user.userName != null -> "@${user.userName}"
        user.lastName != null -> "${user.firstName} ${user.lastName}"
        else -> user.firstName
    }
}

/**
 * Maps a chat into the corresponding enum
 */
fun chatMapper(chat: Chat): Target {
    return when {
        chat.isGroupChat -> Target.GROUP
        chat.isSuperGroupChat -> Target.SUPERGROUP
        chat.isUserChat -> Target.USER
        else -> Target.INVALID
    }
}

/**
 * Returns status of user according to the passed chat
 */
fun getDBStatus(user: User, chat: Chat): Status {
    return when {
        chat.isUserChat -> DatabaseManager.getUser(user.id)?.status ?: Status.NOT_REGISTERED
        chat.isGroupOrSuper() -> DatabaseManager.getGroupUserStatus(groupId = chat.id, userId = user.id)
        else -> Status.NOT_REGISTERED
    }
}

/**
 * Execute function for a collection, defaults to passed value if list is empty or null, or an exception is thrown.
 */
inline fun <E : Any, T : Collection<E>> T?.ifNotEmpty(func: T.() -> Any?, default: Any?): Any? {
    return if (this != null && this.isNotEmpty()) {
        try {
            func(this)
        } catch (e: Exception) {
            default
        }
    } else default
}

/**
 * Print & log functions
 */
fun printK(tag: String, s: Any = "") {
    val d = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    print("$d $tag: $s")
    LoggerK.log("$d $tag: $s")
}

fun printlnK(tag: String, s: Any = "") {
    val d = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    println("$d $tag: $s")
    LoggerK.log("$d $tag: $s")
}

fun logK(tag: String, s: Any = "") {
    val d = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    LoggerK.log("$d $tag: $s")
}

/**
 * Execute script with passed arguments in custom amount of time
 */
fun File.executeScript(
    timeoutAmount: Long,
    timeoutUnit: TimeUnit,
    vararg arguments: String
): String {
    val process = ProcessBuilder(*arguments)
        .directory(this)
        .start()
        .apply { waitFor(timeoutAmount, timeoutUnit) }

    if (process.exitValue() != 0) {
        return process.errorStream.bufferedReader().readText().substringBeforeLast("\n")
    }
    return process.inputStream.bufferedReader().readText().substringBeforeLast("\n")
}

/**
 * Execute script with passed arguments in fixed amount of time
 */
fun File.executeScript(vararg arguments: String): String {
    return this.executeScript(10, TimeUnit.SECONDS, *arguments)
}

/**
 * Convenient way to check if it's a group or supergroup message
 */
fun Message.isGroupOrSuper(): Boolean {
    return this.isGroupMessage || this.isSuperGroupMessage
}

/**
 * Convenient way to check if it's a group or supergroup chat
 */
fun Chat.isGroupOrSuper(): Boolean {
    return this.isSuperGroupChat || this.isGroupChat
}

/**
 * Get current date and time formatted as yyyy-MM-dd_HH-mm-ss
 */
fun getCurrentDateTimeStamp(): String {
    return LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
}

/**
 * Tries to parse a string to get a price, Null if nothing found or an error occurred.
 * Will be changed later: as now it's quite broken.
 */
fun parsePrice(price: String): Float? {
    var mod = price.replace(",", ".")
    if (!mod.contains("."))
        mod = "$mod.00" // simple fix for most common price mismatch
    mod = mod.substringBeforeLast(".").replace(".", "") + "." + mod.substringAfterLast(".") // remove , or . for thousands etc.
    val p = Pattern.compile("(\\d{1,9})*(?:[.,]\\d{2})")
    val m = p.matcher(mod)
    return if (m.find()) m.group().toFloat() else null
}

/**
 * Get username of bot
 */
fun AbsSender.username(): String {
    return if (this is LongPollingBot) {
        this.botUsername
    } else {
        (this as WebhookBot).botUsername
    }
}

/**
 * Get parent folder of java\jar file specified.
 * From: https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
 * Edited.
 * @return path to current folder
 * @throws Exception
 */
@Throws(Exception::class)
fun getMainFolder(): String {
    val kClass = com.kraktun.kbot.Main::class.java
    val codeSource = kClass.protectionDomain.codeSource
    val jarFile: File
    if (codeSource.location != null) {
        jarFile = File(codeSource.location.toURI())
    } else {
        val path = kClass.getResource(kClass.simpleName + ".class").path
        var jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"))
        jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8")
        jarFile = File(jarFilePath)
    }
    return jarFile.parentFile.absolutePath
}