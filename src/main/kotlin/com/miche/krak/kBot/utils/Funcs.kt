package com.miche.krak.kBot.utils

import com.miche.krak.kBot.bots.MainBot
import org.telegram.telegrambots.meta.api.objects.User
import java.io.File
import java.net.URLDecoder



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

/**
 * Get parent folder of java\jar file specified.
 * From: https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
 * Edited.
 * @return path to current folder
 * @throws Exception
 */
@Throws(Exception::class)
fun getMainFolder(): String {
    val kClass = MainBot::class.java
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