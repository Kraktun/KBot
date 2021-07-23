package com.kraktun.kbot.jobs

import com.kraktun.kbot.commands.callbacks.CallbackProcessor
import com.kraktun.kbot.commands.core.MultiCommandsHandler
import com.kraktun.kutils.jobs.MultiJobExecutorCoroutines

/**
 * Executes jobs in set intervals
 */
object JobManager {

    private const val sleepTime = 100L // millis
    private const val threadPool = 5
    private val jobs = mapOf<JobTask, JobInfo>(
        MultiCommandsHandler.CleanerJob() to MultiCommandsHandler.CleanerJob.jobInfo,
        CallbackProcessor.CleanerJob() to CallbackProcessor.CleanerJob.jobInfo
    )
    private val scheduler = MultiJobExecutorCoroutines(threadPool)

    /**
     * Starts threads
     */
    fun run() {
        try {
            Thread.sleep(sleepTime)
            jobs.forEach { (job, info) ->
                addJob(job, info)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Kills threads
     * @return false if an error occurred, true otherwise
     */
    fun shutdown(): Boolean {
        return try {
            Thread.sleep(sleepTime)
            scheduler.stopALl()
            true
        } catch (e: InterruptedException) {
            false
        }
    }

    fun addJob(job: JobTask, info: JobInfo) {
        scheduler.registerTask({ job.execute() }, info.key, info.interval, info.initialDelay)
    }

    fun removeJob(jobInfo: JobInfo) {
        scheduler.stopTask(jobInfo.key)
    }
}
