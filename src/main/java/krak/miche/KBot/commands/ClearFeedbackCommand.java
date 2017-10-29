package krak.miche.KBot.commands;

import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.secondary_Handler.ClearFeedbackHandler;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * @author Kraktun
 * @version 1.0
 * Class to manage clearfeedback command:
 * @todo add inline confirm dialog
 */

public class ClearFeedbackCommand extends BotCommand {
    private static final String LOGTAG = "CLEARFEEDBACKCOMMAND";

    public ClearFeedbackCommand() {
        super( "clearfeedback", "Delete all feedbacks" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Integer userID = user.getId();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if (chat.isUserChat())
        {
            String language = databaseManager.getUserLanguage(user.getId());
            StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("not_super_admin", language));
            if (BuildVars.SUPER_ADMINS.contains(userID))
            {
                ClearFeedbackHandler feddHandler = new ClearFeedbackHandler();
                messageTextBuilder = feddHandler.clearFeedbacks();
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
            String language = databaseManager.getGroupLanguage(chat.getId());
            if (BuildVars.SUPER_ADMINS.contains(user.getId()))
            {
                String message = Localizer.getString("only_user_chat", language);
                SendMessage answer = new SendMessage();
                answer.setChatId(chat.getId().toString());
                answer.setText(message);
                try {
                    absSender.execute(answer);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }
}
