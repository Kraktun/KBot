package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.services.UtilsMain;
import org.telegram.telegrambots.logging.BotLogger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static krak.miche.KBot.BuildVars.DEFAULT_UTC;

/**
 * @author Kraktun
 * @version 1.0
 */

public class ExportHandler {
    private static final String LOGTAG = "EXPORTHANDLER";

    /**
     * Backups users and groups lists in a text file
     * Uses current time as filename
     * @return message with result of operation
     */
    public static StringBuilder write() {
        StringBuilder messageTextBuilder;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT +" + DEFAULT_UTC));
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(cal.getTime());
        try {
            UtilsMain.writeUsers(timeStamp);
            messageTextBuilder = new StringBuilder("File " + timeStamp + " written");
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
            messageTextBuilder = new StringBuilder("Error writing file");
        }
        return messageTextBuilder;
    }

    /**
     * Backups users and groups lists in a text file
     * Uses name  as filename
     * @param name name of the file to save
     * @return message with result of operation
     */
    public static StringBuilder write(String name) {
        StringBuilder messageTextBuilder;
        try {
            UtilsMain.writeUsers(name);
            messageTextBuilder = new StringBuilder("File " + name + " written");
        } catch (Exception e) {
            messageTextBuilder = new StringBuilder("Error writing file");
            BotLogger.error(LOGTAG, e);
        }
        return messageTextBuilder;
    }
}
