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
 * Updates the usernames saved for selected users (admins) both in private and group chats
 */

public class UpdateUsernameCommand extends BotCommand {

    private static final String LOGTAG = "UPDATEUSERNAMESCOMMAND";

    public UpdateUsernameCommand() {
        super( "upduser", "Update Username for admins" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        if (chat.isUserChat())
        {
            Integer userID = user.getId();
            StringBuilder messageTextBuilder = new StringBuilder();
            if (databaseManager.isAdminStatus(userID) || BuildVars.SUPER_ADMINS.contains(userID) || databaseManager.isSuperAdminStatus(userID))
            {
                try {
                    databaseManager.changeUserUsername(userID, UtilsMain.getFormattedUsername(user));
                    messageTextBuilder = new StringBuilder(Localizer.getString("done", language));
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                }
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
        else if (chat.isGroupChat())
        {
            Integer userID = user.getId();
            StringBuilder messageTextBuilder = new StringBuilder();
            if (databaseManager.isUserGroupAdmin(chat.getId(), userID) || BuildVars.SUPER_ADMINS.contains(userID))
            {
                try {
                    databaseManager.changeUserGroupUsername(chat.getId(), userID, UtilsMain.getFormattedUsername(user));
                    messageTextBuilder = new StringBuilder(Localizer.getString("done", language));
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                }
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
    }
}
