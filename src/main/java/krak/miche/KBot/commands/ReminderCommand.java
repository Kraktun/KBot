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
 * Class used to set a reminder. It simply push the first command of the chain.
 */

public class ReminderCommand extends BotCommand {

    private static final String LOGTAG = "REMINDERCOMMAND";

    public ReminderCommand() {
        super( "remindme", "Set a reminder" );
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
                    databaseManager.pushCommand(userID, "remindmeTime");
                    messageTextBuilder = new StringBuilder(Localizer.getString("reminder_form", language));
                    messageTextBuilder.append("\nt dd.hh.mm.ss ");
                    messageTextBuilder.append(Localizer.getString("timer_set", language));
                    messageTextBuilder.append("\nd mm.dd.hh.mm ");
                    messageTextBuilder.append(Localizer.getString("date_set", language));
                    messageTextBuilder.append("\n");
                    messageTextBuilder.append(Localizer.getString("utc_reminder", language));
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                    messageTextBuilder = new StringBuilder(Localizer.getString("error_reminder", language));
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
            if (!databaseManager.isBlackStatus(userID) && databaseManager.isUserGroupAdmin(chat.getId(), userID) && !databaseManager.isUserGroupBlacklisted(chat.getId(), userID))
            {
                try {
                    databaseManager.pushCommand(userID, "remindmeGroupTime");
                    messageTextBuilder = new StringBuilder(Localizer.getString("reminder_form", language));
                    messageTextBuilder.append("\nt dd.hh.mm.ss ");
                    messageTextBuilder.append(Localizer.getString("timer_set", language));
                    messageTextBuilder.append("\nd mm.dd.hh.mm ");
                    messageTextBuilder.append(Localizer.getString("date_set", language));
                    messageTextBuilder.append("\n");
                    messageTextBuilder.append(Localizer.getString("utc_reminder", language));
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                    messageTextBuilder = new StringBuilder(Localizer.getString("error_reminder", language));
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
}
