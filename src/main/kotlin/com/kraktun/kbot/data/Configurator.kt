package com.kraktun.kbot.data

import com.kraktun.kbot.jobs.JobManager

/**
 * Must be initialized before using other parts of the library.
 * When done remember to shutdown.
 */
object Configurator {

    lateinit var dataManager: DataManager
    lateinit var log: (Any) -> Unit

    init {
        JobManager.run()
    }

    fun shutdown() {
        JobManager.shutdown()
    }

    fun withDataManager(d: DataManager): Configurator {
        dataManager = d
        return this
    }

    fun withLogging(func: (Any) -> Unit): Configurator {
        log = func
        return this
    }

    fun isInitialized(): Boolean {
        return ::dataManager.isInitialized && ::log.isInitialized
    }
}
