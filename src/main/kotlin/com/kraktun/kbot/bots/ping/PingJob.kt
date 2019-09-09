package com.kraktun.kbot.bots.ping

import com.kraktun.kbot.PING_BOT_NAME
import com.kraktun.kbot.PING_PONG_CHAT
import com.kraktun.kbot.bots.BotsController
import com.kraktun.kbot.jobs.JobInfo
import com.kraktun.kbot.utils.simpleMessage
import com.kraktun.kbot.utils.username
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext

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
            botList = listOfNotNull(BotsController.getBot(PING_BOT_NAME))
        )
    }

    private val TAG = "PING_JOB"

    override fun execute(context: JobExecutionContext) {
        // printlnK(TAG, "Retrieving articles")
        jobInfo.botList.forEach {
            if (it.username() == PING_BOT_NAME) {
                simpleMessage(it, "ping", PING_PONG_CHAT)
                PingController.registerPing()
            }
        }
    }

    override fun interrupt() {
        // interrupt
    }
}