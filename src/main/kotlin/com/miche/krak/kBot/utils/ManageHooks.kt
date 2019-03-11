package com.miche.krak.kBot.utils

/**
 * What to execute on start
 */
fun onStart() {
    JobExecutor.instance.run()
}

/**
 * What to execute when closing
 */
fun onShutdown() {
    if (!JobExecutor.instance.isShutdown) {
        JobExecutor.instance.shutdown()
    }
}