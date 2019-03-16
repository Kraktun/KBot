package com.miche.krak.kBot.utils

/**
 * What to execute on start
 */
fun onStart() {
    println("Starting system")
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