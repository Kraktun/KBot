package krak.miche.KBot.secondary_Handler;

import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;
import krak.miche.KBot.services.UtilsMain;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;

/**
 * @author Kraktun
 * @version 1.0
 */

public class LogHandler {

    public static final String LOGTAG = "LOGHANDLER";

    public LogHandler() {

    }

    public static boolean logMessage(Message message, boolean isDefault) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String name = getName(message.getFrom());
        if (name.equals(BuildVars.BOT_USERNAME) || name.equals(BuildVars.BOT_TEST_USERNAME))
            return true; //Don't save bot messages
        String date = UtilsMain.getFormattedTime(message.getDate());
        String text = message.getText() + "\n";
        if (isDefault)
            text = text.substring(1, text.length()); //Removes special char
        try {
            databaseManager.insertLog(message.getChatId(), message.getFrom().getId(), name, date, text);
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
            return false;
        }
        return true;
    }

    public static StringBuilder startLog(Long group, String activated) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        try {
            String language = databaseManager.getGroupLanguage(group);
            if (activated.equals("start"))
            {
                databaseManager.setGroupSettingsLog(group, true);
                return new StringBuilder(Localizer.getString("log_active", language));
            }
            else if (activated.equals("stop"))
            {
                databaseManager.setGroupSettingsLog(group, false);
                return new StringBuilder(Localizer.getString("log_stopped", language));
            }
            else
                return new StringBuilder(Localizer.getString("error", language));
        } catch (SQLException e) {
            return new StringBuilder(Localizer.getString("error_log", BuildVars.DEFAULT_LANG));
        }
    }

    public static StringBuilder setLogType(Long group, String type) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        try {
            String language = databaseManager.getGroupLanguage(group);
            databaseManager.updateGroupLogType(group, type);
            return new StringBuilder(Localizer.getString("log_changed", language));
        } catch (SQLException e) {
            return new StringBuilder(Localizer.getString("error_log", BuildVars.DEFAULT_LANG));
        }
    }

    private static String getName(User user) {
        String userSecond = user.getLastName();
        if (userSecond != null  && !userSecond.equals(""))
        {
            return user.getFirstName() + " " + userSecond;
        }
        else
            return user.getFirstName();
    }

    public static boolean isSpecialMessage(Message message) {
        return message.getText().substring(0,1).equals(BuildVars.LOG_SPECIAL_CHAR);
    }

}
