package com.miche.krak.kBot.utils

import org.telegram.telegrambots.meta.api.objects.User
import java.io.File
import java.net.URLDecoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


/**
 * Get identification string for user:
 * if it has a username, use that, if not use first + last name or only first
 */
fun getQualifiedUser(user: User) : String {
    return when {
        user.userName != null -> "@${user.userName}"
        user.lastName != null -> "${user.firstName} ${user.lastName}"
        else -> user.firstName
    }
}

fun printK(tag : String, s : Any = "") {
    print("$tag: $s")
    LoggerK.log("$tag: $s")
}

fun printlnK(tag : String, s : Any = "") {
    println("$tag: $s")
    LoggerK.log("$tag: $s")
}

fun logK(tag : String, s : Any = "") {
    LoggerK.log("$tag: $s")
}

/**
 * Execute script with passed arguments in fixed amount of time
 */
fun File.executeScript(timeoutAmount: Long,
                       timeoutUnit: TimeUnit,
                       vararg arguments: String): String {
    val process = ProcessBuilder(*arguments)
        .directory(this)
        .start()
        .apply { waitFor(timeoutAmount, timeoutUnit) }

    if (process.exitValue() != 0) {
        return process.errorStream.bufferedReader().readText().substringBeforeLast("\n")
    }
    return process.inputStream.bufferedReader().readText().substringBeforeLast("\n")
}

fun File.executeScript(vararg arguments: String): String {
    return this.executeScript(10, TimeUnit.SECONDS, *arguments)
}

/**
 * Get current date and time formatted as yyyy-MM-dd_HH-mm-ss
 */
fun getCurrentDateTimeStamp(): String {
    return LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
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
    val kClass = com.miche.krak.kBot.Main::class.java
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