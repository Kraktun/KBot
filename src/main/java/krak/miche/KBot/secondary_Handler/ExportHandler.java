package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.services.UtilsMain;
import org.telegram.telegrambots.logging.BotLogger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Kraktun
 * @version 1.0
 */

public class ExportHandler {
    private static final String LOGTAG = "EXPORTHANDLER";

    public static StringBuilder write() {
        StringBuilder messageTextBuilder;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+2"));
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
