package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;
import krak.miche.KBot.services.UtilsMain;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;

import static krak.miche.KBot.BuildVars.*;

/**
 * @author Kraktun
 * @version 1.0
 */
public class SetUserGroupHandler {

    public static final String LOGTAG = "SETUSERGROUPHANDLER";
    private DatabaseManager databaseManager = DatabaseManager.getInstance();

    /**
     * Update status for user in selected group
     * @param chatID id of the group
     * @param user user whose status should be changed
     * @param status new status
     * @return message with result of operation
     */
    public StringBuilder updateStatus(Long chatID, User user, String status) {
        StringBuilder messageTextBuilder;
        String language = databaseManager.getGroupLanguage(chatID);
        int userID = user.getId();
        String userName = UtilsMain.getFormattedUsername(user);
        status = status.toUpperCase();
        if (userID != 0)
        {
            if (SUPER_ADMINS.contains(userID))
            {
                messageTextBuilder = new StringBuilder(Localizer.getString("cant_modify_admin", language));
            }
            else if (status.equals(USER_STATUS) || status.equals(POWER_USER_STATUS) || status.equals(ADMIN_STATUS) || status.equals(BLACKLISTED_STATUS))
            {
                if (databaseManager.isUserGroupExist(chatID, userID))
                {
                    try {
                        databaseManager.changeUserGroupStatus(chatID, userID, status);
                        messageTextBuilder = new StringBuilder(Localizer.getString("User", language));
                        messageTextBuilder.append(" ").append(userName).append(Localizer.getString("status_changed", language))
                        .append(" ").append(status);
                    } catch (SQLException e) {
                        BotLogger.error(LOGTAG, e);
                        messageTextBuilder = new StringBuilder(Localizer.getString("error_status", language));
                        messageTextBuilder.append(": ").append(e);
                    }
                }
                else
                {
                    try {
                        databaseManager.addGroupUser(chatID, userID, status, UtilsMain.getFormattedUsername(user), DEFAULT_INFO);
                        messageTextBuilder = new StringBuilder(Localizer.getString("User", language));
                        messageTextBuilder.append(" ").append(userName).append(Localizer.getString("status_changed", language))
                        .append(" ").append(status);
                    } catch (SQLException e) {
                        BotLogger.error(LOGTAG, e);
                        messageTextBuilder = new StringBuilder(Localizer.getString("error_status", language));
                        messageTextBuilder.append(": ").append(e);
                    }
                }
            }
            else
            {
                messageTextBuilder = new StringBuilder(Localizer.getString("syntax_error", language));
            }
            return messageTextBuilder;
        }
        else
        {
            messageTextBuilder = new StringBuilder(Localizer.getString("invalid_input", language));
            return messageTextBuilder;
        }
    }

    /**
     * Update status for user in selected group
     * @param chatID id of the group
     * @param userID ID of the user whose status should be changed
     * @param status new status
     * @return message with result of operation
     */
    public StringBuilder updateStatus(Long chatID, int userID, String status) {
        StringBuilder messageTextBuilder;
        String language = databaseManager.getGroupLanguage(chatID);
        status = status.toUpperCase();
        if (userID != 0)
        {
            if (SUPER_ADMINS.contains(userID))
            {
                messageTextBuilder = new StringBuilder(Localizer.getString("cant_modify_admin", language));
            }
            else if (status.equals(USER_STATUS) || status.equals(POWER_USER_STATUS) || status.equals(ADMIN_STATUS) || status.equals(BLACKLISTED_STATUS))
            {
                if (databaseManager.isUserGroupExist(chatID, userID))
                {
                    try {
                        databaseManager.changeUserGroupStatus(chatID, userID, status);
                        messageTextBuilder = new StringBuilder(Localizer.getString("User", language));
                        messageTextBuilder.append(" ").append(Localizer.getString("status_changed", language))
                        .append(" ").append(status);
                    } catch (SQLException e) {
                        BotLogger.error(LOGTAG, e);
                        messageTextBuilder = new StringBuilder(Localizer.getString("error_status", language));
                        messageTextBuilder.append(": ").append(e);
                    }
                }
                else
                {
                    try {
                        databaseManager.addGroupUser(chatID, userID, status, UNKNOWN_USERNAME, DEFAULT_INFO);
                        messageTextBuilder = new StringBuilder(Localizer.getString("User", language));
                        messageTextBuilder.append(" ").append(Localizer.getString("status_changed", language))
                        .append(" ").append(status);
                    } catch (SQLException e) {
                        BotLogger.error(LOGTAG, e);
                        messageTextBuilder = new StringBuilder(Localizer.getString("error_status", language));
                        messageTextBuilder.append(": ").append(e);
                    }
                }
            }
            else
            {
                messageTextBuilder = new StringBuilder(Localizer.getString("syntax_error", language));
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
