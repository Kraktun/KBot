package com.kraktun.kbot.jobs

import com.kraktun.kutils.log.KLogger
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
        KLogger.flush()
    }

    override fun interrupt() {
    }
}