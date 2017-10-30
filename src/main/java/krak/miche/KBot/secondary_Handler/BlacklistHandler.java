package krak.miche.KBot.secondary_Handler;

import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;

/**
 * @author Kraktun
 * @version 1.0
 */

public class BlacklistHandler {

    public static final String LOGTAG = "BLACKLISTHANDLER";
    private static DatabaseManager databaseManager = DatabaseManager.getInstance();


    public static StringBuilder blacklist(String message, String language) {
        StringBuilder messageTextBuilder;
        if (message == null || message.length() < 1)
        {
            messageTextBuilder = new StringBuilder(Localizer.getString("syntax_error", language));
            return messageTextBuilder;
        }
        else
        {
            try {
                int userBL = Integer.parseInt(message);
                if (databaseManager.isDBUser(userBL))
                {
                    if (BuildVars.SUPER_ADMINS.contains(userBL))
                    {
                        messageTextBuilder = new StringBuilder(Localizer.getString("cant_blacklist_admin", language));
                    }
                    else
                    {
                        try {
                            databaseManager.changeUserStatus(userBL, BuildVars.BLACKLISTED_STATUS);
                            messageTextBuilder = new StringBuilder(Localizer.getString("User", language));
                            messageTextBuilder.append(" ").append(userBL).append(" ").append(Localizer.getString("blacklisted", language));
                        } catch (SQLException e) {
                            BotLogger.error(LOGTAG, e);
                            messageTextBuilder = new StringBuilder(Localizer.getString("error_blacklist", language));
                            messageTextBuilder.append(": ").append(e);
                        }
                    }
                }
                else
                {
                    try {
                        databaseManager.addDBUser(userBL, BuildVars.BLACKLISTED_STATUS, BuildVars.UNKNOWN_USERNAME, BuildVars.DEFAULT_LANG, BuildVars.DEFAULT_UTC, BuildVars.DEFAULT_INFO);
                        messageTextBuilder = new StringBuilder(Localizer.getString("User", language));
                        messageTextBuilder.append(" ").append(userBL).append(" ").append(Localizer.getString("blacklisted", language));
                    } catch (SQLException e) {
                        BotLogger.error(LOGTAG, e);
                        messageTextBuilder = new StringBuilder(Localizer.getString("error_blacklist", language));
                        messageTextBuilder.append(": ").append(e);
                    }
                }
            } catch (NumberFormatException e) {
                messageTextBuilder = new StringBuilder(Localizer.getString("invalid_input", language));
            }
        }
        return messageTextBuilder;
    }
}
