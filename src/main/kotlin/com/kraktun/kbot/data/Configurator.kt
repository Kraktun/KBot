package com.kraktun.kbot.data

import com.kraktun.kbot.jobs.JobManager

/**
 * Must be initialized before using other parts of the library.
 * When done remember to shutdown.
 */
object Configurator {

    val dataManager = mutableMapOf<String, DataManager>()
    lateinit var log: (Any) -> Unit

    init {
        JobManager.run()
    }

    fun shutdown() {
        JobManager.shutdown()
    }

    fun withDataManager(botUsername: String, d: DataManager): Configurator {
        dataManager[botUsername] = d
        return this
    }

    fun withLogging(func: (Any) -> Unit): Configurator {
        log = func
        return this
    }

    fun isInitialized(): Boolean {
        return dataManager.isNotEmpty() && ::log.isInitialized
    }
}
