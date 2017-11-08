package krak.miche.KBot.commands;


import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;

/**
 * @author Kraktun
 * @version 1.0
 * Class to manage feedback command:
 * This only inserts the user in the database
 * command table and asks the user to write the feedback
 * (later it will be managed by processNonCommandUpdates())
 */

public class FeedbackCommand extends BotCommand {
    private static final String LOGTAG = "FEEDBACKCOMMAND";

    public FeedbackCommand() {
        super( "feedback", "Leave a feedback" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Integer userID = user.getId();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("not_allowed", language));
        if (chat.isUserChat())
        {
            if (databaseManager.isDBUser(userID) && !databaseManager.isBlackStatus(userID) && !databaseManager.isRemovedStatus(userID))
            {
                try {
                    databaseManager.pushCommand(userID, "feedback");
                    messageTextBuilder = new StringBuilder(Localizer.getString("type_message", language));
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                    messageTextBuilder = new StringBuilder(Localizer.getString("error_sending_feedback", language));
                }
            }
            else if (databaseManager.isRemovedStatus(userID))
            {
                messageTextBuilder = new StringBuilder(Localizer.getString("restart_bot", language));
            }
            SendMessage answer = new SendMessage();
            answer.setChatId(chat.getId().toString());
            answer.setText(messageTextBuilder.toString());
            try {
                absSender.execute(answer);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        }
        else if (chat.isGroupChat())
        {
            language = databaseManager.getGroupLanguage(chat.getId());
            if (!databaseManager.isBlackStatus(userID))
            {
                messageTextBuilder = new StringBuilder(Localizer.getString("only_user_chat", language));
                SendMessage answer = new SendMessage();
                answer.setChatId(chat.getId().toString());
                answer.setText(messageTextBuilder.toString());
                try {
                    absSender.execute(answer);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }
}
