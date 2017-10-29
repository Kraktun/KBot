package krak.miche.KBot.commands;

import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.secondary_Handler.ExportLogHandler;
import krak.miche.KBot.secondary_Handler.SendFileHandler;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.File;

import static krak.miche.KBot.BuildVars.SUPER_ADMINS;

/**
 * @author Kraktun
 * @version 1.0
 */

public class ExportLogCommand extends BotCommand {
    private static final String LOGTAG = "EXPORTLOGCOMMAND";

    public ExportLogCommand() {
        super( "exportlog", "This command retrieves log from groups" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getGroupLanguage(chat.getId());
        StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("error", language));
        if (chat.isGroupChat())
        {
            File logFile = null;
            if (SUPER_ADMINS.contains(user.getId()) && ( arguments == null || arguments.length == 0 ))
            {
                logFile = ExportLogHandler.write(chat.getId());
            }
            else if (SUPER_ADMINS.contains(user.getId()))
            {
                logFile = ExportLogHandler.write(chat.getId(), arguments[0]);
            }
            if (logFile!=null) {
                SendFileHandler.sendFileToChat(chat, absSender, logFile);
            }
            else {
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
        else if (chat.isUserChat())
        {
            language = databaseManager.getGroupLanguage(chat.getId());
            if (SUPER_ADMINS.contains(user.getId()))
            {
                messageTextBuilder = new StringBuilder(Localizer.getString("only_group_chat", language));
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
}
