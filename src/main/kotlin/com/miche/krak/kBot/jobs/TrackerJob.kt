package com.miche.krak.kBot.jobs

import com.miche.krak.kBot.bots.MainBot
import com.miche.krak.kBot.commands.TrackCommand
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.utils.printlnK
import com.miche.krak.kBot.utils.simpleMessage
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext

class TrackerJob : InterruptableJob {

    companion object {
        val jobInfo = JobInfo(
          name  = "TRACKER_JOB",
          interval  = 3600, //seconds
          trigger  = "TRACKER_JOB_TRIGGER",
          group  = "jobs")
    }

    private val TAG = "TRACKER_JOB"
    private val WAIT_TIME = 15L //seconds

    override fun execute(context: JobExecutionContext) {
        printlnK(TAG, "Retrieving articles")
        DatabaseManager.getAllTrackedObjects().forEach { obj ->
            val list = TrackCommand.getAmazonPrice(domain = obj.domain, articleId = obj.objectId)
            val bestPrice = if (list.isNotEmpty()) list.first() else null
            if (bestPrice == null) printlnK(TAG, "Got a null object for domain ${obj.domain}, id ${obj.objectId}")
            else if (bestPrice.totalPrice() <= obj.targetPrice) {
                simpleMessage(MainBot.instance, "ONE OBJECT HAS REACHED THE TARGET PRICE:\n$bestPrice", obj.user.toLong())
                DatabaseManager.removeTrackedObject(obj)
            }
            Thread.sleep(WAIT_TIME * 1000)
        }
    }

    override fun interrupt() {
        //interrupt
    }
}