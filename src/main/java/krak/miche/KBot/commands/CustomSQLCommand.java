package krak.miche.KBot.commands;


import krak.miche.KBot.BuildVars;
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
 * Class to manage customSQL command:
 * Used only manually, can be quite dangerous
 */

public class CustomSQLCommand extends BotCommand {

    public static final String LOGTAG = "CUSTOMSQLCOMMAND";

    public CustomSQLCommand() {
        super( "customSQL", "Execute a custom SQL command" );

    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("not_admin", language));
        if (chat.isUserChat())
        {
            if (arguments == null || arguments.length < 1)
                messageTextBuilder = new StringBuilder(Localizer.getString("syntax_error", language));
            else if (BuildVars.SUPER_ADMINS.contains(user.getId()))
            {
                String command = "";
                int i = 0;
                while (true)
                {
                    try {
                        command = command + " " + arguments[i];
                        ++i;
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }
                }
                try {
                    databaseManager.executeCustomSQL(command);
                    messageTextBuilder = new StringBuilder(Localizer.getString("done", language));
                } catch (SQLException e) {
                    messageTextBuilder = new StringBuilder(Localizer.getString("error_SQL", language) + " : " + e);
                    BotLogger.error(LOGTAG, e);
                }
            }
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
}
