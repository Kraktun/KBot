package krak.miche.KBot.commands;


import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.secondary_Handler.RemoveUserHandler;
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
 * Static way to remove a user from the database
 * Note: this removes completely the user, it's different from
 * a simple /stop command
 */

public class removeUserCommand extends BotCommand {

    private static final String LOGTAG = "REMOVEUSERCOMMAND";

    public removeUserCommand() {
        super( "rmuser", "This command can be used only by admins to remove users from database \n usage: \n command + [ID]" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("not_super_admin", language));
        if (chat.isUserChat())
        {
            if (BuildVars.SUPER_ADMINS.contains(user.getId()) || databaseManager.isAdminStatus(user.getId()))
            {
                RemoveUserHandler removeHandler = new RemoveUserHandler();
                messageTextBuilder = removeHandler.removeUser(arguments[0], language);
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
