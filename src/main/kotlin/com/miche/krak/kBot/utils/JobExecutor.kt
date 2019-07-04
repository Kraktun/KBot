package com.miche.krak.kBot.utils

import com.miche.krak.kBot.commands.core.MultiCommandsHandler
import com.miche.krak.kBot.jobs.JobInfo
import com.miche.krak.kBot.jobs.LoggerJob
import com.miche.krak.kBot.jobs.TrackerJob
import org.quartz.Job
import org.quartz.JobBuilder.newJob
import org.quartz.SchedulerException
import org.quartz.impl.StdSchedulerFactory
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import java.util.*

/**
 * Executes jobs in set intervals
 */
object JobExecutor {

    private val scheduler = StdSchedulerFactory().scheduler
    private const val sleepTime = 100L // millis
    @Volatile var isShutdown = scheduler.isShutdown
    private val jobs = mapOf<Class<out Job>, JobInfo>(MultiCommandsHandler.CleanerJob::class.java to MultiCommandsHandler.CleanerJob.jobInfo,
        TrackerJob::class.java to TrackerJob.jobInfo,
        LoggerJob::class.java to LoggerJob.jobInfo)

    /**
     * Starts threads
     */
    fun run() {
        try {
            Thread.sleep(sleepTime)
            multiScheduler()
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

    /**
     * Starts thread and run it every interval
     */
    private fun multiScheduler() {
        jobs.forEach { (job, info) ->
            val appendedJob = newJob(job)
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
    }
}