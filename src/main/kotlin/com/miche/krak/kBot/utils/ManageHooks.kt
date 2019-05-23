package com.miche.krak.kBot.utils

import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.predefinedUsers

/**
 * What to execute on start
 */
fun onStart() {
    printlnK("Starting system")
    //Insert predefined users
    printlnK("Adding predefined users")
    printlnK("DB is stored in: ")
    DatabaseManager.instance.insertUser(predefinedUsers)
    printlnK()
    printlnK("Predefined users added")
    JobExecutor.instance.run()
}

/**
 * What to execute when closing
 */
fun onShutdown() {
    printlnK("Closing system")
    if (!JobExecutor.instance.isShutdown) {
        JobExecutor.instance.shutdown()
    }
    LoggerK.instance.flush()
    Thread.sleep(2000)
}