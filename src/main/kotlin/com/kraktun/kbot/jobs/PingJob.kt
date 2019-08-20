package com.kraktun.kbot.jobs

import com.kraktun.kbot.PING_BOT_GROUP
import com.kraktun.kbot.PING_BOT_NAME
import com.kraktun.kbot.bots.PingBot
import com.kraktun.kbot.utils.simpleMessage
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.telegram.telegrambots.meta.bots.AbsSender

class PingJob : InterruptableJob {

    companion object {
        val jobInfo = JobInfo(
            name = "PING_JOB",
            interval = 5100, // seconds
            trigger = "PING_JOB_TRIGGER",
            group = "jobs",
            delay = 120, // seconds
            botList = listOf(PingBot.instance))
    }

    private val TAG = "PING_JOB"
    private val WAIT_TIME = 60L // seconds

    override fun execute(context: JobExecutionContext) {
        // printlnK(TAG, "Retrieving articles")
        jobInfo.botList.forEach {
            if (it.botUsername == PING_BOT_NAME)
                simpleMessage(it as AbsSender, "/ping", PING_BOT_GROUP)
        }
    }

    override fun interrupt() {
        // interrupt
    }
}