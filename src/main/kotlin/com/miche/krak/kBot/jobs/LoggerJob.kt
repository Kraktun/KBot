package com.miche.krak.kBot.jobs

import com.miche.krak.kBot.utils.LoggerK
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext

class LoggerJob : InterruptableJob {

    companion object {
        val jobInfo = JobInfo(
            name = "LOGGER_JOB",
            interval = 120, // seconds
            trigger = "LOGGER_JOB_TRIGGER",
            group = "jobs",
            delay = 120)
    }

    override fun execute(context: JobExecutionContext?) {
        LoggerK.flush()
    }

    override fun interrupt() {
    }
}