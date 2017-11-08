package krak.miche.KBot.commands;

import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;
import krak.miche.KBot.secondary_Handler.BlacklistHandler;
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
 * Class to manage blacklist command:
 * Can be used only by admins and in private chats param is not checked here for consistency
 * Managed error sources: (param = id to blacklist)
 * - not user chat
 * - null or non existing param
 * - command not executed by an admin
 */

public class BlacklistCommand extends BotCommand {
    private static final String LOGTAG = "BLACKLISTCOMMAND";

    public BlacklistCommand() {
        super( "blacklist", "This command can be used only by admins to blacklist users \n usage: \n command + [ID]" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if (chat.isUserChat())
        {
            String language = databaseManager.getUserLanguage(user.getId());
            StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("not_admin", language));
            if (arguments == null || arguments.length == 0)
                messageTextBuilder = new StringBuilder(Localizer.getString("syntax_error", language));
            else if (databaseManager.isAdminStatus(user.getId()) || BuildVars.SUPER_ADMINS.contains(user.getId()))
            {
               messageTextBuilder = BlacklistHandler.blacklist(arguments[0], language);
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
            String language = databaseManager.getGroupLanguage(chat.getId());
            if (databaseManager.isAdminStatus(user.getId()) || BuildVars.SUPER_ADMINS.contains(user.getId()))
            {
                StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("only_user_chat", language));
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
