package krak.miche.KBot.commands;


import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.secondary_Handler.GetAdminsFromGroupHandler;
import krak.miche.KBot.secondary_Handler.ListUsersHandler;
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
 * Get admins of the bot if used in user chat
 * or of the group if used in a group.
 */

public class GetAdminsCommand extends BotCommand {
    private static final String LOGTAG = "GETADMINSCOMMAND";

    public GetAdminsCommand() {
        super( "getadmins", "Get admins list" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("not_allowed", language));
        if (chat.isUserChat())
        {
            ListUsersHandler listUsersHandler = new ListUsersHandler();
            messageTextBuilder = listUsersHandler.getAdmins();
        }
        else if (chat.isGroupChat())
        {
            messageTextBuilder = GetAdminsFromGroupHandler.getAdmins(chat.getId());
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
