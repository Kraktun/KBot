package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;

/**
 * @author Kraktun
 * @version 1.0
 */

public class ListUsersHandler {

    public static final String LOGTAG = "LISTUSERSHANDLER";
    private static DatabaseManager databaseManager = DatabaseManager.getInstance();

    /**
     * Get a list of all the users that started the bot
     * @return list of all the users
     */
    public static StringBuilder getUsers() {
        StringBuilder messageTextBuilder;
        try {
            String users = databaseManager.getAllUsers();
            messageTextBuilder = new StringBuilder("USERS LIST:\n" + users);
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
            messageTextBuilder = new StringBuilder("Error retrieving users list");
        }
        return messageTextBuilder;
    }

    /**
     * Get all admins of the bot
     * @return List of all admins of the bot
     */
    public static StringBuilder getAdmins(String language) {
        StringBuilder messageTextBuilder;
        try {
            String users = databaseManager.getAllAdmins();
            messageTextBuilder = new StringBuilder("ADMINS LIST:\n" + users);
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
            messageTextBuilder = new StringBuilder(Localizer.getString("error_getting_admin3", language));
        }
        return messageTextBuilder;
    }
}
