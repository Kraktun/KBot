package krak.miche.KBot.Jobs;

import krak.miche.KBot.secondary_Handler.AntiFloodHandler;
import krak.miche.KBot.services.UtilsMain;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Kraktun
 * @version 1.0
 * Wrapper class to manage Antiflood thread
 */
public class AntifloodJob implements InterruptableJob {

    public static final String LOGTAG = "ANTIFLOODJOB";

    public void execute(JobExecutionContext context) throws JobExecutionException {
        AntiFloodHandler antiFloodHandler = AntiFloodHandler.getInstance();
        antiFloodHandler.execute();
    }

    public void interrupt() {
        AntiFloodHandler antiFloodHandler = AntiFloodHandler.getInstance();
        antiFloodHandler.interrupt();
        UtilsMain.log("Antiflood interrupted");
    }

}
