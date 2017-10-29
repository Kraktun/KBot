package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;

/**
 * @author Kraktun
 * @version 1.0
 */

public class ListUsersHandler {
    public static final String LOGTAG = "LISTUSERSHANDLER";
    private DatabaseManager databaseManager = DatabaseManager.getInstance();

    public ListUsersHandler() {
    }

    public StringBuilder getUsers() {
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

    public StringBuilder getAdmins() {
        StringBuilder messageTextBuilder;
        try {
            String users = databaseManager.getAllAdmins();
            messageTextBuilder = new StringBuilder("ADMINS LIST:\n" + users);
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
            messageTextBuilder = new StringBuilder("Error retrieving admins list");
        }
        return messageTextBuilder;
    }
}
