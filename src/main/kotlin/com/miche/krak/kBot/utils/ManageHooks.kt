package com.miche.krak.kBot.utils

fun onStart() {
    JobExecutor.instance.run()
}

fun onShutdown() {
    if (!JobExecutor.instance.isShutdown) {
        JobExecutor.instance.shutdown()
    }
}