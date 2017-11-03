package krak.miche.KBot.commands;


import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.keyboards.SatoolsKeyboard;
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
 * Initialize a special keyboard for admins (WIP)
 */

public class satoolsCommand extends BotCommand {

    private static final String LOGTAG = "SATOOLSCOMMAND";

    public satoolsCommand() {
        super( "satools", "Tools for admins" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        int userID = user.getId();
        StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("not_super_admin", language));
        SendMessage answer = new SendMessage();
        answer.setText(messageTextBuilder.toString());
        if (chat.isUserChat())
        {
            if (BuildVars.SUPER_ADMINS.contains(user.getId()))
            {
                try {
                    databaseManager.pushCommand(userID, "satools");
                    answer.setReplyMarkup(SatoolsKeyboard.sendCustomKeyboard());
                    answer.setText("Choose an option:");
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                    answer.setText(Localizer.getString("error_satools", language));
                }
            }
            answer.setChatId(chat.getId().toString());
            try {
                absSender.execute(answer);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        }
        else if (chat.isGroupChat())
        {
            language = databaseManager.getGroupLanguage(chat.getId());
            if (BuildVars.SUPER_ADMINS.contains(user.getId()))
            {
                answer.setText(Localizer.getString("only_user_chat", language));
                answer.setChatId(chat.getId().toString());
                try {
                    absSender.execute(answer);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }
}
