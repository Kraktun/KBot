package com.kraktun.kbot.jobs

import com.kraktun.kbot.PING_BOT_GROUP
import com.kraktun.kbot.PING_BOT_NAME
import com.kraktun.kbot.bots.PingBot
import com.kraktun.kbot.utils.simpleMessage
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.telegram.telegrambots.meta.bots.AbsSender

// TODO: Write which job are enabled in DB and restore on boot (keep track of how many bot require them to remove inactive ones).
//  Also keep track of which bot (username) to link them again in botList (needs a mapper username -> absSender)
class PingJob : InterruptableJob {

    companion object {
        val jobInfo = JobInfo(
            name = "PING_JOB",
            interval = 15, // seconds
            trigger = "PING_JOB_TRIGGER",
            group = "jobs",
            delay = 10, // seconds
            botList = listOf(PingBot.instance))
    }

    private val TAG = "PING_JOB"

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