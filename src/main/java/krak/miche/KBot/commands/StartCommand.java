package krak.miche.KBot.commands;

import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.handler.CommandsHandler;
import krak.miche.KBot.secondary_Handler.InitializeHandler;
import krak.miche.KBot.services.UtilsMain;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kraktun
 * @version 1.0
 * Reloads admins list if used in groups.
 * If used in user chat adds the user to the DB
 */

public class StartCommand extends BotCommand {

    public static final String LOGTAG = "STARTCOMMAND";

    public StartCommand() {
        super( "start", "With this command you can start the Bot" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        StringBuilder messageBuilder = new StringBuilder();
        Long chatId = chat.getId();
        String username = UtilsMain.getFormattedUsername(user);
        if (chat.isUserChat())
        {
            String language = databaseManager.getUserLanguage(user.getId());
            if (databaseManager.isDBUser(user.getId()))
            {
                if (databaseManager.isRemovedStatus(user.getId()))
                {
                    try {
                        databaseManager.changeUserStatus(user.getId(), BuildVars.USER_STATUS);
                    } catch (SQLException e) {
                        BotLogger.error(LOGTAG, e);
                    }
                }
                messageBuilder.append(Localizer.getString("hello_command", language)).append(username).append("\n");
                messageBuilder.append(Localizer.getString("hello_existing", language));
            }
            else
            {
                try {
                    databaseManager.addDBUser(user.getId(), BuildVars.USER_STATUS, username, BuildVars.DEFAULT_LANG, BuildVars.DEFAULT_UTC, BuildVars.DEFAULT_INFO);
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                }
                messageBuilder.append(Localizer.getString("hello_command", language)).append(username).append("\n");
                messageBuilder.append(Localizer.getString("hello_not_existing", language));
            }
            SendMessage answer = new SendMessage();
            answer.setChatId(chat.getId().toString());
            answer.setText(messageBuilder.toString());
            try {
                absSender.execute(answer);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        }
        else if (chat.isGroupChat())
        {
            String language = BuildVars.DEFAULT_LANG;
            if (isAdminGroup(absSender, chatId, user.getId()))
            {
                if (databaseManager.isDBGroup(chatId))
                {
                    language = databaseManager.getGroupLanguage(chat.getId());
                    //Reinitialize admin list if some new admins have been added
                    InitializeHandler initializeHandler = new InitializeHandler();
                    initializeHandler.addAdminsGroup(absSender, chatId, user.getId());
                    messageBuilder.append(Localizer.getString("done", language));
                }
                else
                {
                    InitializeHandler initializeHandler = new InitializeHandler();
                    try {
                        databaseManager.addGroup(chatId);
                        initializeHandler.startUserGroupTable(chatId);
                        initializeHandler.startGroupSettings(chatId);
                        initializeHandler.addAdminsGroup(absSender, chatId, user.getId());
                        messageBuilder.append(Localizer.getString("group_added", language));
                        messageBuilder.append("\n");
                        messageBuilder.append(Localizer.getString("group_added_plus", language));
                    } catch (SQLException e) {
                        BotLogger.error(LOGTAG, e);
                        messageBuilder.append(Localizer.getString("error_adding_group", language));
                    }
                }
                SendMessage answer = new SendMessage();
                answer.setChatId(chat.getId().toString());
                answer.setText(messageBuilder.toString());
                answer.enableHtml(true);
                try {
                    absSender.execute(answer);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }

    private boolean isAdminGroup(AbsSender absSender, long groupID, int user) {
        GetChatAdministrators getAdmins = new GetChatAdministrators();
        getAdmins.setChatId(groupID);
        List<Integer> adminsID = new ArrayList<>();
        try {
            List<ChatMember> admins = absSender.execute(getAdmins);
            for (ChatMember admin : admins)
            {
                adminsID.add(admin.getUser().getId());
            }
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
        return adminsID.contains(user);
    }
}
