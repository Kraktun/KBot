package com.miche.krak.kBot.utils

import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.predefinedUsers

private const val TAG = "MANAGE_HOOKS"
/**
 * What to execute on start
 */
fun onStart() {
    printlnK(TAG, "Starting system")
    printlnK(TAG, "Current version is: ${com.miche.krak.kBot.Main::class.java.getPackage().implementationVersion}")
    //Insert predefined users
    printlnK(TAG, "Adding predefined users")
    DatabaseManager.insertUser(predefinedUsers)
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