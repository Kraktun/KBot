package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.handler.CommandsHandler;
import krak.miche.KBot.database.DatabaseManager;

import static krak.miche.KBot.BuildVars.*;

import krak.miche.KBot.services.Localizer;
import krak.miche.KBot.services.UtilsMain;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kraktun
 * @version 1.0
 */

public class InitializeHandler {

    public static final String LOGTAG = "INITIALIZEHANDLER";

    public static StringBuilder startUsersTable() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        StringBuilder messageTextBuilder;
        try {
            databaseManager.initializeUsers();
            messageTextBuilder = new StringBuilder("Database Initialized");
        } catch (SQLException e) {
            //Check for pre-existing table not necessary
            messageTextBuilder = new StringBuilder("Error in database initialization + \n");
            BotLogger.error(LOGTAG, e);
        }
        int i = 0;
        while (SUPER_ADMINS.size() > i)
        {
            if (databaseManager.isDBUser(SUPER_ADMINS.get(i)) && !databaseManager.isSuperAdminStatus(SUPER_ADMINS.get(i)))
            {
                try {
                    databaseManager.changeUserStatus(SUPER_ADMINS.get(i), SUPER_ADMIN_STATUS);
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                    messageTextBuilder.append("Error changing to SUPER_ADMIN: " + e);
                }
            }
            else if (!databaseManager.isDBUser(SUPER_ADMINS.get(i)))
            {
                try {
                    databaseManager.addDBUser(SUPER_ADMINS.get(i), SUPER_ADMIN_STATUS, SUPER_ADMINS_USERNAMES.get(i), DEFAULT_LANG, DEFAULT_UTC, DEFAULT_INFO);
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                    messageTextBuilder.append("Error adding SUPER_ADMIN: " + e);
                }
            }
            ++i;
        }
        i = 0;
        while (ADMINS.size() > i)
        {
            if (databaseManager.isDBUser(ADMINS.get(i)) && !databaseManager.isAdminStatus(ADMINS.get(i)))
            {
                try {
                    databaseManager.changeUserStatus(ADMINS.get(i), ADMIN_STATUS);
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                    messageTextBuilder.append("Error changing to ADMIN: " + e);
                }
            }
            else if (!databaseManager.isDBUser(ADMINS.get(i)))
            {
                try {
                    databaseManager.addDBUser(ADMINS.get(i), ADMIN_STATUS, ADMINS_USERNAMES.get(i), DEFAULT_LANG, DEFAULT_UTC, DEFAULT_INFO);
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                    messageTextBuilder.append("Error adding ADMIN: " + e);
                }
            }
            ++i;
        }
        i = 0;
        while (POWER_USER.size() > i)
        {
            if (databaseManager.isDBUser(POWER_USER.get(i)) && !databaseManager.isPowerUserStatus(POWER_USER.get(i)))
            {
                try {
                    databaseManager.changeUserStatus(POWER_USER.get(i), POWER_USER_STATUS);
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                    messageTextBuilder.append("Error changing to POWER_USER: " + e);
                }
            }
            else if (!databaseManager.isDBUser(POWER_USER.get(i)))
            {
                try {
                    databaseManager.addDBUser(POWER_USER.get(i), POWER_USER_STATUS, POWER_USER_USERNAMES.get(i), DEFAULT_LANG, DEFAULT_UTC, DEFAULT_INFO);
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                    messageTextBuilder.append("Error adding POWER_USER: " + e);
                }
            }
            ++i;
            i = 0;
            while (BLACK_LIST.size() > i)
            {
                if (databaseManager.isDBUser(BLACK_LIST.get(i)) && !databaseManager.isBlackStatus(BLACK_LIST.get(i)))
                {
                    try {
                        databaseManager.changeUserStatus(BLACK_LIST.get(i), BLACKLISTED_STATUS);
                    } catch (SQLException e) {
                        BotLogger.error(LOGTAG, e);
                        messageTextBuilder.append("Error changing to BLACKLISTED: " + e);
                    }
                }
                else if (!databaseManager.isDBUser(BLACK_LIST.get(i)))
                {
                    try {
                        databaseManager.addDBUser(BLACK_LIST.get(i), BLACKLISTED_STATUS, BLACK_LIST_USERNAMES.get(i), DEFAULT_LANG, DEFAULT_UTC, DEFAULT_INFO);
                    } catch (SQLException e) {
                        BotLogger.error(LOGTAG, e);
                        messageTextBuilder.append("Error adding BLACKLISTED: " + e);
                    }
                }
                ++i;
            }
        }
        return messageTextBuilder;
    }

    public static StringBuilder startFeedbackTable() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        StringBuilder message2;
        try {
            databaseManager.initializeFeedback();
            message2 = new StringBuilder("Feedback table Initialized");
        } catch (SQLException e) {
            //Check for pre-existing table not necessary
            message2 = new StringBuilder("Error in Feedback table initialization + \n");
            BotLogger.error(LOGTAG, e);
        }
        return message2;
    }

    public static StringBuilder startLogsTable() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        StringBuilder message2;
        try {
            databaseManager.initializeLogs();
            message2 = new StringBuilder("Log table Initialized");
        } catch (SQLException e) {

            //Check for pre-existing table not necessary
            message2 = new StringBuilder("Error in Log table initialization + \n");
            BotLogger.error(LOGTAG, e);
        }
        return message2;
    }

    public static StringBuilder startCommandsTable() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        StringBuilder message3;
        try {
            databaseManager.initializeCommandTable();
            message3 = new StringBuilder("Commands table Initialized");
        } catch (SQLException e) {
            //Check for pre-existing table not necessary
            message3 = new StringBuilder("Error in Commands table initialization + \n");
            BotLogger.error(LOGTAG, e);
        }
        return message3;
    }

    public static StringBuilder startGroupTable() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        StringBuilder message4;
        try {
            databaseManager.initializeGroupTable();
            message4 = new StringBuilder("Group table Initialized");
        } catch (SQLException e) {

            //Check for pre-existing table not necessary
            message4 = new StringBuilder("Error in Group table initialization + \n");
            BotLogger.error(LOGTAG, e);
        }
        return message4;
    }

    public static StringBuilder startUserGroupTable(Long groupID) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        StringBuilder message4;
        try {
            databaseManager.initializeUserGroupTable(groupID);
            message4 = new StringBuilder("User Group table Initialized \n");
        } catch (SQLException e) {

            //Check for pre-existing table not necessary
            message4 = new StringBuilder("Error in User Group table initialization \n");
            BotLogger.error(LOGTAG, e);
        }
        try {
            databaseManager.initializeGroupSettingsTable(groupID);
            message4.append("Group Settings table Initialized");
        } catch (SQLException e) {
            //Check for pre-existing table not necessary
            message4.append("Error in Group Settings table initialization");
            BotLogger.error(LOGTAG, e);
        }
        return message4;
    }

    public static void addAdminsGroup(AbsSender absSender, Long groupID, int user) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        GetChatAdministrators getAdmins = new GetChatAdministrators();
        getAdmins.setChatId(groupID);
        List<User> adminsUsers = new ArrayList<>();
        List<Integer> adminsID = new ArrayList<>();
        try {
            List<ChatMember> admins = absSender.execute(getAdmins);
            for (ChatMember admin : admins)
            {
                adminsUsers.add(admin.getUser());
                adminsID.add(admin.getUser().getId());
            }
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
            String messageTextBuilder2 = "Error retrieving admins list";
            SendMessage answer = new SendMessage();
            answer.setChatId(groupID.toString());
            answer.setText(messageTextBuilder2);
            try {
                CommandsHandler.getInstance().execute(answer);
            } catch (TelegramApiException ec) {
                BotLogger.error(LOGTAG, ec);
            }
        }
        //adminsID contains at least the creator of the group so it can't be empty
        if (adminsID.contains(user))
        {
            StringBuilder message2;
            if (!databaseManager.isDBGroup(groupID))
            {
                message2 = startUserGroupTable(groupID);
                SendMessage answer2 = new SendMessage();
                answer2.setChatId(groupID.toString());
                answer2.setText(message2.toString());
                try {
                    CommandsHandler.getInstance().execute(answer2);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
            String language = databaseManager.getGroupLanguage(groupID);
            //Adds admins to database
            StringBuilder message3 = new StringBuilder(Localizer.getString("admins_added_group", language));
            for (User anAdmin : adminsUsers)
            {
                int num = anAdmin.getId();
                if (!databaseManager.isUserGroupExist(groupID, num))
                    try{
                        databaseManager.addGroupUser(groupID, num, ADMIN_STATUS, UtilsMain.getFormattedUsername(anAdmin), DEFAULT_INFO);
                    } catch (SQLException e) {
                        message3 = new StringBuilder(Localizer.getString("error_adding_admins_group", language));
                        BotLogger.error(LOGTAG, e);
                    }
                else
                {
                    try {
                        databaseManager.changeUserGroupStatus(groupID, num, ADMIN_STATUS);
                    } catch (SQLException ex) {
                        message3 = new StringBuilder(Localizer.getString("error_status", language));
                        BotLogger.error(LOGTAG, ex);
                    }
                }
            }
            SendMessage answer3 = new SendMessage();
            answer3.setChatId(groupID.toString());
            answer3.setText(message3.toString());
            try {
                CommandsHandler.getInstance().execute(answer3);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        }
    }

    public static void startGroupSettings(Long groupID) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        try {
            databaseManager.initializeGroupSettings(groupID);
            BotLogger.info(LOGTAG, "Group" + groupID + " Settings Initialized");
        } catch (SQLException e) {
            //Check for pre-existing table not necessary
            BotLogger.error(LOGTAG, "Error in Group table initialization: \n" + e);
        }
    }
}
