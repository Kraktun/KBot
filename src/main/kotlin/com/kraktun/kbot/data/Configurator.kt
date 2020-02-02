package com.kraktun.kbot.data

import com.kraktun.kbot.jobs.JobExecutor
import com.kraktun.kbot.jobs.LoggerJob

object Configurator {

    lateinit var dataManager: DataManager

    fun withDataManager(d: DataManager) : Configurator {
        dataManager = d
        return this
    }

    fun withLoggerJob() : Configurator {
        JobExecutor.addJob(LoggerJob(), LoggerJob.jobInfo)
        return this
    }
}