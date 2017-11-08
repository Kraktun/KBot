package krak.miche.KBot.commands;

import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;
import krak.miche.KBot.secondary_Handler.ExportHandler;
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
 * Class to manage export command:
 * Saves a list of admins, blacklisted users ecc. in a text file
 * Note: saves only Bot users, not for groups
 */

public class ExportCommand extends BotCommand {
    private static final String LOGTAG = "EXPORTCOMMAND";

    public ExportCommand() {
        super( "export", "This command backups admins list usage: \ncommand + [file name]" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("not_super_admin", language));
        if (chat.isUserChat())
        {
            if (BuildVars.SUPER_ADMINS.contains(user.getId()) && ( arguments == null || arguments.length == 0 ))
            {
                messageTextBuilder = ExportHandler.write();
            }
            else if (BuildVars.SUPER_ADMINS.contains(user.getId()))
            {
                messageTextBuilder = ExportHandler.write(arguments[0]);
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
            if (BuildVars.SUPER_ADMINS.contains(user.getId()))
            {
                messageTextBuilder = new StringBuilder(Localizer.getString("only_user_chat", language));
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
