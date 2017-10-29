package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;

/**
 * @author Kraktun
 * @version 1.0
 */

public class ListGroupsHandler {
    public static final String LOGTAG = "LISTUSERSHANDLER";
    private DatabaseManager databaseManager = DatabaseManager.getInstance();

    public ListGroupsHandler() {
    }

    public StringBuilder getGroups() {
        StringBuilder messageTextBuilder;
        try {
            String groups = databaseManager.getAllGroups();
            messageTextBuilder = new StringBuilder("GROUPS LIST:\n" + groups);
        } catch (SQLException e) {
            messageTextBuilder = new StringBuilder("Error retrieving groups list");
            BotLogger.error(LOGTAG, e);
        }
        return messageTextBuilder;
    }
}
