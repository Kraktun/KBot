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
 * Used to set bot language for groups
 */

public class LanguageCommand extends BotCommand {

    private static final String LOGTAG = "LANGUAGESCOMMAND";

    public LanguageCommand() {
        super( "lang", "Used to set bot language in groups" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        if (chat.isGroupChat())
        {
            language = databaseManager.getGroupLanguage(chat.getId());
            Integer userID = user.getId();
            StringBuilder messageTextBuilder;
            if (databaseManager.isUserGroupAdmin(chat.getId(), userID) && arguments.length > 0)
            {
                String newLanguage = arguments[0];
                if (newLanguage.equalsIgnoreCase("en") || newLanguage.equalsIgnoreCase("it"))
                {
                    newLanguage = newLanguage.toLowerCase();
                    try {
                        databaseManager.updateGroupSettingsLanguage(chat.getId(), newLanguage);
                    } catch (SQLException e) {
                        BotLogger.error(LOGTAG, e);
                    }
                    messageTextBuilder = new StringBuilder(Localizer.getString("done", newLanguage));
                }
                else
                    messageTextBuilder = new StringBuilder(Localizer.getString("syntax_error", language));
            }
            else
                messageTextBuilder = new StringBuilder(Localizer.getString("syntax_error", language));
            SendMessage answer = new SendMessage();
            answer.setChatId(chat.getId().toString());
            answer.setText(messageTextBuilder.toString());
            try {
                absSender.execute(answer);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        }
        else if (chat.isUserChat())
        {
            StringBuilder messageTextBuilder;
            messageTextBuilder = new StringBuilder(Localizer.getString("use_settings", language));
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
