package krak.miche.KBot.secondary_Handler;


import org.telegram.telegrambots.logging.BotLogger;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static krak.miche.KBot.services.UtilsMain.writeUsers;

/**
 * @author Kraktun
 * @version 1.0
 */

public class ExportHandler {
    private static final String LOGTAG = "EXPORTHANDLER";
    private StringBuilder messageTextBuilder;

    public ExportHandler() {
    }

    public StringBuilder write() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(cal.getTime());
        try {
            writeUsers(timeStamp);
            messageTextBuilder = new StringBuilder("File " + timeStamp + " written");
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
            messageTextBuilder = new StringBuilder("Error writing file");
        }
        return messageTextBuilder;
    }

    public StringBuilder write(String name) {
        try {
            writeUsers(name);
            messageTextBuilder = new StringBuilder("File " + name + " written");
        } catch (Exception e) {
            messageTextBuilder = new StringBuilder("Error writing file");
            BotLogger.error(LOGTAG, e);
        }
        return messageTextBuilder;
    }
}
