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

public class RemoveUserHandler {

    public static final String LOGTAG = "REMOVEUSERSHANDLER";
    private static DatabaseManager databaseManager = DatabaseManager.getInstance();

    /**
     * Removes user from DB (user is removed from DB, not simply set as 'REMOVED')
     * @param message message containing the ID of the user to remove
     * @param language language of the user who sent the command
     * @return result of operation
     */
    public static StringBuilder removeUser(String message, String language) {
        StringBuilder messageTextBuilder;
        if (message == null || message.length() < 1)
        {
            messageTextBuilder = new StringBuilder(Localizer.getString("syntax_error", language));
            return messageTextBuilder;
        }
        else
        {
            try {
                int userREM = Integer.parseInt(message);
                if (databaseManager.isDBUser(userREM))
                {
                    if (BuildVars.SUPER_ADMINS.contains(userREM))
                    {
                        messageTextBuilder = new StringBuilder(Localizer.getString("cant_remove_admin", language));
                    }
                    else
                    {
                        try {
                            databaseManager.removeUser(userREM);
                            messageTextBuilder = new StringBuilder(Localizer.getString("cant_remove_admin", language))
                                                 .append(userREM).append(Localizer.getString("removed", language));
                        } catch (SQLException e) {
                            BotLogger.error(LOGTAG, e);
                            messageTextBuilder = new StringBuilder(Localizer.getString("error_status", language))
                                                 .append(": " + e);
                        }
                    }
                }
                else
                {
                    messageTextBuilder = new StringBuilder(Localizer.getString("User", language));
                    messageTextBuilder.append(" ").append(userREM).append(Localizer.getString("not_in_database", language));
                }

            } catch (NumberFormatException e) {
                BotLogger.error(LOGTAG, e);
                messageTextBuilder = new StringBuilder(Localizer.getString("invalid_input", language));
            }
        }
        return messageTextBuilder;
    }
}
