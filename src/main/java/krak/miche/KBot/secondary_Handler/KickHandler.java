package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.services.Localizer;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.UtilsMain;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * @author Kraktun
 * @version 1.0
 */
public class KickHandler {

    public static final String LOGTAG = "KICKHANDLER";

    /**
     * Removes user from the group
     * @param user user to remove
     * @param chat chat (group) where the user should be removed
     * @param absSender bot that received the update
     * @return result of operation
     */
    public static StringBuilder kickUser(User user, Chat chat, AbsSender absSender) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getGroupLanguage(chat.getId());
        StringBuilder messageTextBuilder;
        int userBan = user.getId();
        Long groupID = chat.getId();
        try {
            removeFromChat(groupID, userBan, absSender);
            messageTextBuilder = new StringBuilder(Localizer.getString("remove_user_success", language));
            messageTextBuilder.append(": ").append(UtilsMain.getFormattedUsername(user));
        } catch (TelegramApiException ex)   {
            BotLogger.error(LOGTAG, ex);
            messageTextBuilder = new StringBuilder(Localizer.getString("remove_user_fail", language));
            messageTextBuilder.append(": ").append(UtilsMain.getFormattedUsername(user));
        }
        return messageTextBuilder;
    }

    /**
     * Removes user from the group
     * @param groupID id of the group
     * @param userID id of the user to remove
     * @param absSender bot that received the update
     * @throws TelegramApiException if an error occurred
     */
    private static void removeFromChat(Long groupID, int userID, AbsSender absSender) throws TelegramApiException {
        KickChatMember kickuser = new KickChatMember(groupID, userID);
        absSender.execute(kickuser);
    }
}
