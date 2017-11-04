package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.logging.BotLogger;
import static krak.miche.KBot.BuildVars.*;

import java.sql.SQLException;

/**
 * @author Kraktun
 * @version 1.0
 */

public class ChangeStatusHandler {

    public static final String LOGTAG = "CHANGESTATUSHANDLER";

    /**
     * Changes user status in database
     * @param user id of the user
     * @param status new status for the user
     * @param language language of the user who sent the command
     * @return result of operation
     */
    public static StringBuilder updateStatus(String user, String status, String language) {
        DatabaseManager databseManager = DatabaseManager.getInstance();
        StringBuilder messageTextBuilder;
        int userID;
        try {
            userID = Integer.parseInt(user);
        } catch (NumberFormatException e)   {
            return new StringBuilder(Localizer.getString("error_blacklist", language));
        }
        status = status.toUpperCase();
        if (userID != 0)
        {
            if (databseManager.isDBUser(userID))
            {
                if (SUPER_ADMINS.contains(userID))
                {
                    messageTextBuilder = new StringBuilder(Localizer.getString("cant_modify_admin", language));
                }
                else if (status.equals(USER_STATUS) || status.equals(POWER_USER_STATUS) || status.equals(ADMIN_STATUS) || status.equals(BLACKLISTED_STATUS))
                {
                    try {
                        databseManager.changeUserStatus(userID, status);
                        messageTextBuilder = new StringBuilder(Localizer.getString("User", language));
                        messageTextBuilder.append(" ").append(userID).append(" ").append(Localizer.getString("status_changed", language))
                        .append(" ").append(status);
                    } catch (SQLException e) {
                        BotLogger.error(LOGTAG, e);
                        messageTextBuilder = new StringBuilder(Localizer.getString("error_status", language));
                        messageTextBuilder.append(": ").append(e);
                    }
                }
                else
                {
                    messageTextBuilder = new StringBuilder(Localizer.getString("syntax_error", language));
                }
            }
            else
            {
                messageTextBuilder = new StringBuilder(Localizer.getString("User", language));
                messageTextBuilder.append(" ").append(userID).append(Localizer.getString("not_in_database", language));
            }
            return messageTextBuilder;
        }
        else
        {
            messageTextBuilder = new StringBuilder(Localizer.getString("invalid_input", language));
            return messageTextBuilder;
        }
    }
}
