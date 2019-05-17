package com.miche.krak.kBot.utils

import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.predefinedUsers

/**
 * What to execute on start
 */
fun onStart() {
    println("Starting system")
    //Insert predefined users
    println("Adding predefined users")
    println("DB is stored in: ")
    DatabaseManager.instance.insertUser(predefinedUsers)
    println()
    println("Predefined users added")
    JobExecutor.instance.run()
}

/**
 * What to execute when closing
 */
fun onShutdown() {
    println("Closing system")
    if (!JobExecutor.instance.isShutdown) {
        JobExecutor.instance.shutdown()
    }
    Thread.sleep(2000)
}