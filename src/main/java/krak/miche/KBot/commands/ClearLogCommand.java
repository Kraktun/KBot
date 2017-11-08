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
 * Manages clearlog command
 * Used to delete all messages logged for the group where it is sent
 * @todo add some way to confirm the deletion
 */

public class ClearLogCommand extends BotCommand {

    private static final String LOGTAG = "LOGCOMMAND";

    public ClearLogCommand() {
        super( "clearlog", "Only available in group chats" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        StringBuilder rispondi;
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if (chat.isGroupChat())
        {
            String language = databaseManager.getGroupLanguage(chat.getId());
            if (( BuildVars.SUPER_ADMINS.contains(user.getId()) || databaseManager.isUserGroupAdmin(chat.getId(), user.getId()) ))
            {
                try {
                    databaseManager.clearLogForGroup(chat.getId());
                    rispondi = new StringBuilder(Localizer.getString("done", language));
                } catch (SQLException e) {
                    rispondi = new StringBuilder(Localizer.getString("error", language));
                    BotLogger.error(LOGTAG, e);
                }
            }
            else
                rispondi = new StringBuilder(Localizer.getString("not_allowed", language));
        }
        else
        {
            String language = databaseManager.getUserLanguage(user.getId());
            rispondi = new StringBuilder(Localizer.getString("only_group_chat", language));
        }
        SendMessage answer = new SendMessage();
        answer.setChatId(chat.getId().toString());
        answer.setText(rispondi.toString());
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
