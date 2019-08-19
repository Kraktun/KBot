package com.kraktun.kbot.utils

import com.kraktun.kbot.database.DatabaseManager
import com.kraktun.kbot.predefinedUsers

private const val TAG = "MANAGE_HOOKS"
/**
 * What to execute on start
 */
fun onStart() {
    printlnK(TAG, "Starting system")
    printlnK(TAG, "Current version is: ${com.kraktun.kbot.Main::class.java.getPackage().implementationVersion}")
    // Insert predefined users
    printlnK(TAG, "Adding predefined users")
    DatabaseManager.addUser(predefinedUsers)
    printlnK(TAG, "Predefined users added")
    JobExecutor.run()
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