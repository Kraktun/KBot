package com.miche.krak.kBot.jobs

import com.miche.krak.kBot.bots.MainBot
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.trackingServices.AmazonService
import com.miche.krak.kBot.utils.printlnK
import com.miche.krak.kBot.utils.simpleHTMLMessage
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.telegram.telegrambots.meta.bots.AbsSender

class TrackerJob : InterruptableJob {

    companion object {
        val jobInfo = JobInfo(
          name  = "TRACKER_JOB",
          interval  = 3600, //seconds
          trigger  = "TRACKER_JOB_TRIGGER",
          group  = "jobs",
          delay = 120,
          botList = listOf(MainBot.instance))
    }

    private val TAG = "TRACKER_JOB"
    private val WAIT_TIME = 15L //seconds

    override fun execute(context: JobExecutionContext) {
        //printlnK(TAG, "Retrieving articles")
        DatabaseManager.getAllTrackedObjects().forEach { obj ->
            when (obj.store) {
                AmazonService().getName() -> {
                    val bestPrice = AmazonService.filterPrices(obj)
                    if (bestPrice != null && bestPrice.totalPrice() <= obj.targetPrice) {
                        jobInfo.botList.forEach {
                            simpleHTMLMessage(it as AbsSender, "The object <b>${obj.name}</b> has reached the target price (${obj.targetPrice}):\n$bestPrice", obj.user.toLong())
                        }
                        DatabaseManager.removeTrackedObject(obj)
                    }
                    Thread.sleep(WAIT_TIME * 1000)
                }
            }
        }
    }

    override fun interrupt() {
        //interrupt
    }
}