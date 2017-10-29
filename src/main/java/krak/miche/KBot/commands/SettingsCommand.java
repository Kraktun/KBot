package krak.miche.KBot.commands;


import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.keyboards.SettingsKeyboard;
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
 * Used to manage settings: initializes the settings keyboard
 */

public class SettingsCommand extends BotCommand {

    private static final String LOGTAG = "SETTINGSCOMMAND";

    public SettingsCommand() {
        super( "settings", "Used to define settings" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        if (chat.isUserChat())
        {
            Integer userID = user.getId();
            StringBuilder messageTextBuilder;
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            String language = databaseManager.getUserLanguage(user.getId());
            if (!databaseManager.isBlackStatus(userID))
            {
                messageTextBuilder = new StringBuilder(Localizer.getString("settings_start", language));
                try {
                    databaseManager.pushCommand(userID, "settings");
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                }
                SendMessage answer = new SendMessage();
                answer.setChatId(chat.getId().toString());
                answer.setText(messageTextBuilder.toString());
                answer.setReplyMarkup(SettingsKeyboard.sendCustomKeyboard());
                try {
                    absSender.execute(answer);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }
}
