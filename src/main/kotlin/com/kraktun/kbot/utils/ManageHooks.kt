package com.kraktun.kbot.utils

import com.kraktun.kbot.database.DatabaseManager
import com.kraktun.kbot.predefinedUsers
import java.io.File

private const val TAG = "MANAGE_HOOKS"
/**
 * What to execute on start
 */
fun onStart() {
    printlnK(TAG, "Starting system")
    printlnK(TAG, "Current version is: ${com.kraktun.kbot.Main::class.java.getPackage().implementationVersion}")
    // Create dirs
    buildDirs()
    // Insert predefined users
    printlnK(TAG, "Adding predefined users")
    DatabaseManager.addUser(predefinedUsers)
    printlnK(TAG, "Predefined users added")
    JobExecutor.run()
}

private fun buildDirs() {
    val logs = File("${getMainFolder()}/logs")
    if (!logs.exists() || !logs.isDirectory) {
        logK(TAG, "Creating logs folder")
        logs.mkdir()
    }
    val downloads = File("${getMainFolder()}/downloads")
    if (!downloads.exists() || !downloads.isDirectory) {
        logK(TAG, "Creating downloads folder")
        downloads.mkdir()
    }
}

/**
 * What to execute when closing
 */
fun onShutdown() {
    printlnK(TAG, "Closing system")
    if (!JobExecutor.isShutdown) {
        JobExecutor.shutdown()
    }
    LoggerK.flush()
    Thread.sleep(2000)
}