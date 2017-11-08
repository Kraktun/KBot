package krak.miche.KBot.Jobs;

import krak.miche.KBot.secondary_Handler.ReminderHandler;
import krak.miche.KBot.services.UtilsMain;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Kraktun
 * @version 1.0
 * Wrapper class to manage Reminders thread
 */
public class RemindersJob implements InterruptableJob {

    public static final String LOGTAG = "REMINDERSJOB";

    public void execute(JobExecutionContext context) throws JobExecutionException {
        ReminderHandler antiFloodHandler = ReminderHandler.getInstance();
        antiFloodHandler.execute();
    }

    public void interrupt() {
        ReminderHandler antiFloodHandler = ReminderHandler.getInstance();
        antiFloodHandler.interrupt();
        UtilsMain.log("RemindersJob interrupted");
    }
}
