package com.kraktun.kbot.jobs

import com.kraktun.kbot.commands.callbacks.CallbackProcessor
import com.kraktun.kbot.commands.core.MultiCommandsHandler
import org.quartz.Job
import org.quartz.JobBuilder.newJob
import org.quartz.JobKey
import org.quartz.SchedulerException
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.TriggerKey
import org.quartz.impl.StdSchedulerFactory
import java.util.*

/**
 * Executes jobs in set intervals
 */
object JobManager {

    private val scheduler = StdSchedulerFactory().scheduler
    private const val sleepTime = 100L // millis
    @Volatile var isShutdown = scheduler.isShutdown
    private val jobs = mapOf<Job, JobInfo>(
        MultiCommandsHandler.CleanerJob() to MultiCommandsHandler.CleanerJob.jobInfo,
        CallbackProcessor.CleanerJob() to CallbackProcessor.CleanerJob.jobInfo
    )

    /**
     * Starts threads
     */
    fun run() {
        try {
            Thread.sleep(sleepTime)
            jobs.forEach { (job, info) ->
                addJob(job, info)
            }
            scheduler.start()
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
            scheduler.shutdown(true)
            true
        } catch (e: SchedulerException) {
            false
        } catch (e: InterruptedException) {
            false
        }
    }

    fun addJob(job: Job, info: JobInfo) {
        val appendedJob = newJob(job::class.java)
            .withIdentity(info.name, info.group)
            .build()
        // Trigger the job to run now, and then every n seconds
        val startingTime = Calendar.getInstance()
        startingTime.add(Calendar.SECOND, info.delay)
        val trigger = newTrigger()
            .withIdentity(info.trigger, info.group)
            .startAt(startingTime.time)
            .withSchedule(
                simpleSchedule()
                    .withIntervalInSeconds(info.interval)
                    .repeatForever()
            )
            .forJob(appendedJob)
            .build()
        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(appendedJob, trigger)
    }

    fun removeJob(jobInfo: JobInfo) {
        scheduler.interrupt(JobKey.jobKey(jobInfo.trigger, jobInfo.group))
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobInfo.trigger, jobInfo.group))
    }
}
