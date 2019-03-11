package com.miche.krak.kBot.jobs

import org.quartz.JobExecutionException
import org.quartz.JobExecutionContext
import org.quartz.InterruptableJob

/**
 * Test for jobs
 */
class TestJob : InterruptableJob {

    companion object {
        const val name : String = "TEST"
        const val interval : Int = 100
        const val trigger : String = "TEST_TRIGGER"
        const val group : String = "group1"
    }


    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {
        //execute something
    }

    override fun interrupt() {
        //interrupt
    }
}