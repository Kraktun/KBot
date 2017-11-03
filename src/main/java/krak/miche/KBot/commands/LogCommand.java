package krak.miche.KBot.commands;

import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.secondary_Handler.LogHandler;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import static krak.miche.KBot.BuildVars.SUPER_ADMINS;

/**
 * @author Kraktun
 * @version 1.0
 * Manages logging options for groups:
 * If used followed by start/stop it enables or disables logging
 * If used followed by default/invert it changes the way it stores messages as
 * explained in the help message
 */

public class LogCommand extends BotCommand {

    private static final String LOGTAG = "LOGCOMMAND";

    public LogCommand() {
        super( "log", "Only available in group chats" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        StringBuilder rispondi;
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if (chat.isGroupChat())
        {
            String language = databaseManager.getGroupLanguage(chat.getId());
            String command = "";
            int i = 0;
            while (i < 2)
            {
                try {
                    command = command + " " + arguments[i];
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
                ++i;
            }
            if (( SUPER_ADMINS.contains(user.getId()) || databaseManager.isUserGroupAdmin(chat.getId(), user.getId()) ))
            {
                if (i > 0) //i = last argument index
                {
                    String param1 = arguments[0];
                    if (param1.equals("start") || param1.equals("stop"))
                    {
                        rispondi = LogHandler.startLog(chat.getId(), param1);
                    }
                    else if (param1.equals("default") || param1.equals("invert"))
                    {
                        rispondi = LogHandler.setLogType(chat.getId(), param1);
                    }
                    else
                        rispondi = new StringBuilder(Localizer.getString("syntax_error", language));
                }
                else
                    rispondi = new StringBuilder(Localizer.getString("syntax_error", language));
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