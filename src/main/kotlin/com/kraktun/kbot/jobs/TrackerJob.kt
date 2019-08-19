package com.kraktun.kbot.jobs

import com.kraktun.kbot.bots.MainBot
import com.kraktun.kbot.database.DatabaseManager
import com.kraktun.kbot.services.tracking.AmazonService
import com.kraktun.kbot.services.tracking.UnieuroService
import com.kraktun.kbot.utils.simpleHTMLMessage
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.telegram.telegrambots.meta.bots.AbsSender
import kotlin.random.Random

class TrackerJob : InterruptableJob {

    companion object {
        val jobInfo = JobInfo(
            name = "TRACKER_JOB",
            interval = 5100, // seconds
            trigger = "TRACKER_JOB_TRIGGER",
            group = "jobs",
            delay = 120, // seconds
            botList = listOf(MainBot.instance))
    }

    private val TAG = "TRACKER_JOB"
    private val WAIT_TIME = 15L // seconds

    override fun execute(context: JobExecutionContext) {
        // printlnK(TAG, "Retrieving articles")
        DatabaseManager.getAllTrackedObjects().forEach { obj ->
            when (obj.store) {
                AmazonService().getName() -> {
                    Thread.sleep((WAIT_TIME + Random.nextInt(until = 300)) * 1000) // make checks a bit more random
                    val bestPrice = AmazonService.filterPrices(obj)
                    if (bestPrice != null && bestPrice.totalPrice() <= obj.targetPrice) {
                        jobInfo.botList.forEach {
                            simpleHTMLMessage(it as AbsSender, "The object <b>${obj.name}</b> has reached the target price (${obj.targetPrice}):\n$bestPrice", obj.user.toLong())
                        }
                        DatabaseManager.removeTrackedObject(obj)
                    }
                }
                UnieuroService().getName() -> {
                    Thread.sleep((WAIT_TIME + Random.nextInt(until = 300)) * 1000) // make checks a bit more random
                    val price = UnieuroService.getPrice(obj.objectId)
                    if (price != null && price.totalPrice() <= obj.targetPrice) {
                        jobInfo.botList.forEach {
                            simpleHTMLMessage(it as AbsSender, "The object <b>${obj.name}</b> has reached the target price (${obj.targetPrice}):\n$price", obj.user.toLong())
                        }
                        DatabaseManager.removeTrackedObject(obj)
                    }
                }
            }
        }
    }

    override fun interrupt() {
        // interrupt
    }
}