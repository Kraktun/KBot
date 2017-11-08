package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.handler.CommandsHandler;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * @author Kraktun
 * @version 1.0
 */

public class GetAdminsFromGroupHandler {
    public static final String LOGTAG = "GETUSERSFROMGROUPHANDLER";

    /**
     * Get a list of the admins in selected group from DB in the form
     * USERNAME (or first + last name) : STATUS
     * @param groupID id of the group
     * @return List of all admins
     */
    public static StringBuilder getAdmins(Long groupID) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        StringBuilder messageTextBuilder;
        String language = databaseManager.getGroupLanguage(groupID);
        try {
            messageTextBuilder = new StringBuilder("ADMINS LIST:\n");
            String users = databaseManager.getAdminsFromGroup(groupID);
            String risultato = "";
            //users contains admins as:
            //88888888888 : ADMIN
            //so we change the number to the corresponding username or first+last name
            Scanner inn = new Scanner(users);
            while (inn.hasNextLine())
            {
                try {
                    int num = Integer.parseInt(inn.next()); //contains the ID of the admin
                    GetChatMember admin = new GetChatMember();
                    admin.setChatId(groupID);
                    admin.setUserId(num);
                    ChatMember adminM = CommandsHandler.getInstance().execute(admin);
                    User adminU = adminM.getUser();
                    //get username or first name + last name
                    String userName = adminU.getUserName();
                    boolean noCocc = false; //check if @ is necessary
                    if (userName == null || userName.isEmpty())
                    {
                        noCocc = true;
                        if (adminU.getLastName() == null)
                            userName = adminU.getFirstName();
                        else
                            userName = adminU.getFirstName() + " " + adminU.getLastName();
                    }
                    if (!noCocc)
                        userName = "@" + userName;
                    risultato = risultato + userName + inn.nextLine() + "\n";
                } catch (NumberFormatException e)   {
                    BotLogger.error(LOGTAG, e);
                    messageTextBuilder = new StringBuilder(Localizer.getString("error_getting_admin1", language));
                } catch (TelegramApiException ex)   {
                    BotLogger.error(LOGTAG, ex);
                    messageTextBuilder = new StringBuilder(Localizer.getString("error_getting_admin2", language));
                }
            }
            messageTextBuilder.append(risultato);
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
            messageTextBuilder = new StringBuilder(Localizer.getString("error_getting_admin3", language));
        }
        return messageTextBuilder;
    }
}
