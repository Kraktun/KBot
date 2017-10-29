package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;

/**
 * @author Kraktun
 * @version 1.0
 */

public class RetrieveFeedbackHandler {

    public static final String LOGTAG = "RETRIEVEFEEDBACKHANDLER";
    private DatabaseManager databaseManager = DatabaseManager.getInstance();

    public RetrieveFeedbackHandler() {
    }

    public StringBuilder getFeedbacks() {
        StringBuilder messageTextBuilder;
        try {
            String feedbacks = databaseManager.getAllFeedbacks();
            messageTextBuilder = new StringBuilder("FEEDBACKS:\n" + feedbacks);
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
            messageTextBuilder = new StringBuilder("Error retrieving feedbacks list");
        }
        return messageTextBuilder;
    }
}
