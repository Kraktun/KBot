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
    private static DatabaseManager databseManager = DatabaseManager.getInstance();

    /**
     * Prepares threads (reminders and antiflood) to be stopped
     * Calls interrupt of both
     * Then disconnects the DB
     * @return message with result of operation
     */
    public static StringBuilder poweroff() {
        UtilsMain.killBots();
        shutJobs();
        StringBuilder messageTextBuilder;
        ReminderHandler reminderHandler = ReminderHandler.getInstance();
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

    /**
     * Removes user from command table before disconnecting the database
     * to avoid problem on boot
     * @param userID ID of the user to remove
     */
    public static void preOFF(int userID) throws SQLException {
        databseManager.removeUserCommand(userID);
    }

    /**
     * Shutdown threads for antiflood and reminders
     */
    private static void shutJobs() {
        JobExecutor jobExecutor = JobExecutor.getInstance();
        if (!jobExecutor.isShutdown())
            jobExecutor.shutdown();
    }
}
