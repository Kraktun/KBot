package com.miche.krak.kBot.utils

import com.miche.krak.kBot.jobs.TestJob
import org.quartz.JobBuilder.newJob
import org.quartz.SchedulerException
import org.quartz.impl.StdSchedulerFactory
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.Scheduler
import org.quartz.TriggerBuilder.newTrigger

/**
 * Executes jobs in set intervals
 */
class JobExecutor private constructor(){

    companion object {
        val instance by lazy {JobExecutor()}
    }

    private var scheduler: Scheduler = StdSchedulerFactory().scheduler
    private var sleepTime : Long = 100
    var isShutdown : Boolean = scheduler.isShutdown

    /**
     * Starts threads
     */
    fun run() {
        try {
            Thread.sleep(sleepTime)
            runTest()
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
    private fun runTest() {
        // define the job and tie it to our class
        val testJob = newJob(TestJob::class.java)
            .withIdentity(TestJob.name, TestJob.group)
            .build()
        // Trigger the job to run now, and then every n seconds
        val trigger = newTrigger()
            .withIdentity(TestJob.trigger, TestJob.group)
            .startNow()
            .withSchedule(
                simpleSchedule()
                    .withIntervalInSeconds(TestJob.interval)
                    .repeatForever()
            )
            .forJob(testJob)
            .build()
        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(testJob, trigger)
    }

    /**
     * Interrupts test
     */
    private fun preShutdown() {

    }
}