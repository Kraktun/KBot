package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.services.Localizer;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.handler.CommandsHandler;
import krak.miche.KBot.services.UtilsMain;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * @author Kraktun
 * @version 1.0
 */

public class KickHandler {
    public static final String LOGTAG = "KICKHANDLER";
    DatabaseManager databaseManager = DatabaseManager.getInstance();

    public KickHandler() {

    }

    public StringBuilder kickUser(User user, Chat chat) {
        String language = databaseManager.getGroupLanguage(chat.getId());
        StringBuilder messageTextBuilder;
        int userBan = user.getId();
        Long groupID = chat.getId();
        try {
            removeFromChat(groupID, userBan);
            messageTextBuilder = new StringBuilder(Localizer.getString("remove_user_success", language));
            messageTextBuilder.append(": ").append(UtilsMain.getFormattedUsername(user));
        } catch (TelegramApiException ex)   {
            BotLogger.error(LOGTAG, ex);
            messageTextBuilder = new StringBuilder(Localizer.getString("remove_user_fail", language));
            messageTextBuilder.append(": ").append(UtilsMain.getFormattedUsername(user));
        }
        return messageTextBuilder;
    }

    private void removeFromChat(Long groupID, int userID) throws TelegramApiException {
        KickChatMember kickuser = new KickChatMember(groupID, userID);
        CommandsHandler.getInstance().execute(kickuser);
    }
}
