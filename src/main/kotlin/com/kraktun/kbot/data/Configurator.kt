package com.kraktun.kbot.data

object Configurator {

    lateinit var dataManager: DataManager
    lateinit var log: (Any) -> Unit

    fun withDataManager(d: DataManager) : Configurator {
        dataManager = d
        return this
    }

    fun withLogging( func: (Any) -> Unit) : Configurator {
        log = func
        return this
    }

    fun isInitialized() : Boolean {
        return ::dataManager.isInitialized && ::log.isInitialized
    }
}