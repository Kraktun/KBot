package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;

/**
 * @author Kraktun
 * @version 1.0
 */

public class ClearFeedbackHandler {

    public static final String LOGTAG = "CLEARFEEDBACKHANDLER";
    private DatabaseManager databaseManager = DatabaseManager.getInstance();

    public ClearFeedbackHandler() {
    }

    public StringBuilder clearFeedbacks() {
        StringBuilder messageTextBuilder;
        try {
            databaseManager.clearFeedback();
            try {
                databaseManager.initializeFeedback();
                messageTextBuilder = new StringBuilder("Table Recreated");
            } catch (SQLException e)   {
                BotLogger.error(LOGTAG, e);
                messageTextBuilder = new StringBuilder("Error Recreating table");
            }
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
            messageTextBuilder = new StringBuilder("Error Destroying table");
        }
        return messageTextBuilder;
    }

}
