package com.miche.krak.kBot.utils

import com.miche.krak.kBot.commands.core.MultiCommandsHandler
import com.miche.krak.kBot.jobs.JobInfo
import com.miche.krak.kBot.jobs.TrackerJob
import org.quartz.Job
import org.quartz.JobBuilder.newJob
import org.quartz.SchedulerException
import org.quartz.impl.StdSchedulerFactory
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.Scheduler
import org.quartz.TriggerBuilder.newTrigger

/**
 * Executes jobs in set intervals
 */
object JobExecutor {

    private var scheduler: Scheduler = StdSchedulerFactory().scheduler
    private var sleepTime : Long = 100
    var isShutdown : Boolean = scheduler.isShutdown
    val jobs = mapOf<Class<out Job>, JobInfo>(MultiCommandsHandler.CleanerJob::class.java to MultiCommandsHandler.CleanerJob.jobInfo, TrackerJob::class.java to TrackerJob.jobInfo)

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
        preShutdown()
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
     * @throws Exception if an error occurred
     */
    @Throws(Exception::class)
    private fun multiScheduler() {
        jobs.forEach { job, info ->
            val trackerJob = newJob(job)
                .withIdentity(info.name, info.group)
                .build()
            // Trigger the job to run now, and then every n seconds
            val trigger = newTrigger()
                .withIdentity(info.trigger, info.group)
                .startNow()
                .withSchedule(
                    simpleSchedule()
                        .withIntervalInSeconds(info.interval)
                        .repeatForever()
                )
                .forJob(trackerJob)
                .build()
            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(trackerJob, trigger)
        }
    }

    /**
     * Interrupts
     */
    private fun preShutdown() {

    }
}