package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.JobExecutor;
import krak.miche.KBot.services.UtilsMain;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;

/**
 * @author Kraktun
 * @version 1.0
 */

public class ShutdownHandler {
    public static final String LOGTAG = "SHUTDOWNHANDLER";
    private DatabaseManager databseManager = DatabaseManager.getInstance();

    public ShutdownHandler() {
    }

    public StringBuilder poweroff() {
        UtilsMain.killBots();
        StringBuilder messageTextBuilder;
        ReminderHandler reminderHandler = ReminderHandler.getInstance();
        reminderHandler.interrupt();
        reminderHandler.backupReminders();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            BotLogger.error(LOGTAG, e);
        }
        try {
            databseManager.shutdown();
            messageTextBuilder = new StringBuilder("Database Disconnected");
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
            messageTextBuilder = new StringBuilder("Error in database disconnection");
        }
        return messageTextBuilder;
    }

    //removes user from command table before disconnecting the database
    public void preOFF(int userID) throws SQLException {
        databseManager.removeUserCommand(userID);
    }

    public void shutJobs() {
        JobExecutor jobExecutor = JobExecutor.getInstance();
        if (!jobExecutor.isShutdown())
            jobExecutor.shutdown();
    }
}
