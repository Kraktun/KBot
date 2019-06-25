package com.miche.krak.kBot.jobs

import com.miche.krak.kBot.bots.MainBot
import com.miche.krak.kBot.commands.TrackCommand
import com.miche.krak.kBot.database.DatabaseManager
import com.miche.krak.kBot.utils.simpleMessage
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

class TrackerJob : InterruptableJob {

    companion object {
        const val name : String = "TRACKER_JOB"
        const val interval : Int = 3600 //seconds
        const val trigger : String = "TRACKER_JOB_TRIGGER"
        const val group : String = "jobs"
    }

    private val TAG = "TRACKER_JOB"
    private val WAIT_TIME = 15L //seconds

    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {
        DatabaseManager.getAllTrackedObjects().forEach { obj ->
            val bestPrice = TrackCommand.getPrices(domain = obj.domain, articleId = obj.objectId).first()
            if (bestPrice.totalPrice() <= obj.targetPrice)
                simpleMessage(MainBot.instance, "ONE OBJECT HAS REACHED THE TARGET PRICE:\n$bestPrice", obj.user.toLong())
            Thread.sleep(WAIT_TIME * 1000)
        }

    }

    override fun interrupt() {
        //interrupt
    }
}