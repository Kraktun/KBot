package krak.miche.KBot.commands;

import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.secondary_Handler.ChangeStatusHandler;
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
 * Static way to promote/demote users
 */

public class setUserCommand extends BotCommand {

    private static final String LOGTAG = "SETUSERCOMMAND";

    public setUserCommand() {
        super( "setuser", "This command can be used only by admins to promote users \n usage: \n command + [ID] + [position] \n " +
               "where position = \n[USER]\n[POWER_USER]\n[ADMIN]\n[BLACKLISTED]" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("not_admin", language));
        if (chat.isUserChat())
        {
            if (arguments == null || arguments.length < 2)
                messageTextBuilder = new StringBuilder(Localizer.getString("syntax_error", language));
            else if (databaseManager.isAdminStatus(user.getId()) || BuildVars.SUPER_ADMINS.contains(user.getId()))
            {
                String status = arguments[1].toUpperCase();
                String userTarget = arguments[0];
                try {
                    messageTextBuilder = ChangeStatusHandler.updateStatus(userTarget, status, language);
                } catch (NumberFormatException e) {
                    messageTextBuilder = new StringBuilder(Localizer.getString("invalid_input", language));
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
        else if (chat.isGroupChat())
        {
            language = databaseManager.getGroupLanguage(chat.getId());
            if (databaseManager.isAdminStatus(user.getId()) || BuildVars.SUPER_ADMINS.contains(user.getId()))
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
