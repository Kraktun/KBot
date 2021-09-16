package com.kraktun.kbot.data

import com.kraktun.kbot.jobs.JobManager

/**
 * Must be initialized before using other parts of the library.
 * When done remember to shut down.
 */
object Configurator {

    val dataManager = mutableMapOf<String, DataManager>()
    lateinit var log: (Any) -> Unit
    var threadPool: Int = 5

    init {
        JobManager.run()
    }

    fun shutdown() {
        JobManager.shutdown()
    }

    /**
     * Add one data manager for a specific bot
     * The same data manager can be shared by multiple bots
     *
     * @param botUsername the unique username of the bot
     * @param d the data manager to associate to this bot
     * @return the configurator
     */
    fun withDataManager(botUsername: String, d: DataManager): Configurator {
        dataManager[botUsername] = d
        return this
    }

    fun withLogging(func: (Any) -> Unit): Configurator {
        log = func
        return this
    }

    fun withThreadPool(threadPool: Int): Configurator {
        this.threadPool = threadPool
        return this
    }

    fun isInitialized(): Boolean {
        return dataManager.isNotEmpty() && ::log.isInitialized
    }
}
