package krak.miche.KBot.commands;

import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;
import krak.miche.KBot.services.UtilsMain;
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
 * Set user as REMOVED in DB, the user is not removed from the database unless you
 * explicitly use /rmuser
 */

public class StopCommand extends BotCommand {

    public static final String LOGTAG = "STOPCOMMAND";

    public StopCommand() {
        super( "stop", "With this command you can stop the Bot" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        int userID = user.getId();
        String userName = UtilsMain.getFormattedUsername(user);
        StringBuilder text;
        if (chat.isUserChat())
        {
            if (databaseManager.isDBUser(userID))
            {
                try {
                    databaseManager.changeUserStatus(userID, BuildVars.REMOVED_STATUS);
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
            text = new StringBuilder(Localizer.getString("goodbye", language) + userName + "\n" + Localizer.getString("goodbye_plus", language));
            SendMessage answer = new SendMessage();
            answer.setChatId(chat.getId().toString());
            answer.setText(text.toString());
            try {
                absSender.execute(answer);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        }
        else if (chat.isGroupChat())
        {
            language = databaseManager.getGroupLanguage(chat.getId());
            if (databaseManager.isUserGroupAdmin(chat.getId(), userID))
            {
                text = new StringBuilder(Localizer.getString("stop_group", language));
                SendMessage answer = new SendMessage();
                answer.setChatId(chat.getId().toString());
                answer.setText(text.toString());

                try {
                    absSender.execute(answer);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }
}
