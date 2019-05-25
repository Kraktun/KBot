package com.miche.krak.kBot.utils

import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.predefinedUsers

private const val TAG = "MANAGE_HOOKS"
/**
 * What to execute on start
 */
fun onStart() {
    printlnK(TAG, "Starting system")
    //Insert predefined users
    printlnK(TAG, "Adding predefined users")
    printlnK(TAG, "DB is stored in: ")
    DatabaseManager.instance.insertUser(predefinedUsers)
    printlnK("\n$TAG")
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