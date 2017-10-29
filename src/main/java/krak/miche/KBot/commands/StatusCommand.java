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

/**
 * @author Kraktun
 * @version 1.0
 * Get your status both in user chat and group chat
 */

public class StatusCommand extends BotCommand {

    private static final String LOGTAG = "STATUSCOMMAND";

    public StatusCommand() {
        super( "status", "This command returns your status (admin/power user/user)" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Integer userID = user.getId();
        String status;
        StringBuilder messageTextBuilder;
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        messageTextBuilder = new StringBuilder(Localizer.getString("Not_in_database", language));
        if (chat.isUserChat())
        {
            if (databaseManager.isDBUser(userID))
            {
                status = databaseManager.getUserStatus(userID);
                if (status != null)
                    messageTextBuilder = new StringBuilder(Localizer.getString("you_are_a", language)).append(status);
                else
                    messageTextBuilder = new StringBuilder(Localizer.getString("error", language));
            }
        }
        else if (chat.isGroupChat())
        {
            language = databaseManager.getGroupLanguage(chat.getId());
            if (databaseManager.isDBGroup(chat.getId()))
            {
                if (databaseManager.isUserGroupExist(chat.getId(), userID))
                {
                    status = databaseManager.getGroupUserStatus(chat.getId(), userID);
                    if (status != null)
                        messageTextBuilder = new StringBuilder(Localizer.getString("you_are_a", language)).append(status);
                    else
                    {
                        messageTextBuilder = new StringBuilder(Localizer.getString("error", language));
                    }
                }
                else
                    messageTextBuilder = new StringBuilder(Localizer.getString("you_are_a", language) + BuildVars.USER_STATUS);
            }
            else
                messageTextBuilder = new StringBuilder(Localizer.getString("Not_in_database", language));
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
