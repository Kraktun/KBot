package com.miche.krak.kBot.utils

import com.miche.krak.kBot.commands.core.MultiCommandsHandler
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

    /**
     * Starts threads
     */
    fun run() {
        try {
            Thread.sleep(sleepTime)
            runMultiCommandsCleaner()
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
    private fun runMultiCommandsCleaner() {
        // define the job and tie it to our class
        val cleanerJob = newJob(MultiCommandsHandler.CleanerJob::class.java)
            .withIdentity(MultiCommandsHandler.CleanerJob.name, MultiCommandsHandler.CleanerJob.group)
            .build()
        // Trigger the job to run now, and then every n seconds
        val trigger = newTrigger()
            .withIdentity(MultiCommandsHandler.CleanerJob.trigger, MultiCommandsHandler.CleanerJob.group)
            .startNow()
            .withSchedule(
                simpleSchedule()
                    .withIntervalInSeconds(MultiCommandsHandler.CleanerJob.interval)
                    .repeatForever()
            )
            .forJob(cleanerJob)
            .build()
        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(cleanerJob, trigger)
    }

    /**
     * Interrupts
     */
    private fun preShutdown() {

    }
}