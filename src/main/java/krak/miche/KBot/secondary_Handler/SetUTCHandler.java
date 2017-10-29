package krak.miche.KBot.secondary_Handler;

import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * @author Kraktun
 * @version 1.0
 */

public class SetUTCHandler {

    private static final String LOGTAG = "SETUTCHANDLER";

    public SetUTCHandler() {

    }

    public static StringBuilder insertUTC(boolean isGroup, String utcTime, long ID) {
        StringBuilder messageTextBuilder;
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language;
        if (isGroup)
            language = databaseManager.getGroupLanguage(ID);
        else
            language = databaseManager.getUserLanguage(Long.valueOf(ID).intValue());
        try {
            int time = Integer.parseInt(utcTime);
            if (time < 14 && time > -12)   //according to https://en.wikipedia.org/wiki/List_of_time_zones_by_country
            {
                try {
                    if (isGroup)
                        databaseManager.updateGroupUTC(ID, time + "");
                    else
                        databaseManager.changeUserUTC(Long.valueOf(ID).intValue(), time);
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                }
                messageTextBuilder = new StringBuilder(Localizer.getString("new_UTC", language));
                messageTextBuilder.append("\n");
                LocalDateTime localNow = LocalDateTime.now(Clock.systemUTC());
                if (isGroup)
                    localNow = localNow.plusHours(Integer.parseInt(databaseManager.getGroupUTC(ID)));
                else
                    localNow = localNow.plusHours(databaseManager.getUserUTC(Long.valueOf(ID).intValue()));
                int hour = localNow.getHour();
                int minute = localNow.getMinute();
                String ans1 = ( hour < 10 ? "0" : "" ) + hour;
                String ans2 = ( minute < 10 ? "0" : "" ) + minute;
                messageTextBuilder.append(ans1).append(".").append(ans2);
                messageTextBuilder.append("\n");
                messageTextBuilder.append(Localizer.getString("wrong_UTC", language));
            }
            else
                messageTextBuilder = new StringBuilder(Localizer.getString("invalid_input", language));
        } catch (NumberFormatException e) {
            messageTextBuilder = new StringBuilder(Localizer.getString("syntax_error", language));
        } catch (Exception e) {
            messageTextBuilder = new StringBuilder(Localizer.getString("error", language));
        }
        return messageTextBuilder;
    }
}
