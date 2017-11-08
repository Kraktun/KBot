package krak.miche.KBot.handler;

import krak.miche.KBot.BotConfig;
import krak.miche.KBot.commands.*;
import krak.miche.KBot.secondary_Handler.*;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.database.SQLUtil;
import krak.miche.KBot.keyboards.SettingsKeyboard;
import krak.miche.KBot.services.Localizer;
import krak.miche.KBot.services.UtilsMain;
import krak.miche.KBot.structures.CommandObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.*;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import static krak.miche.KBot.handler.HandlerUtil.*;
import static krak.miche.KBot.services.UtilsMain.*;

import static krak.miche.KBot.BuildVars.*;
import static krak.miche.KBot.structures.CommandsStatic.GROUP_COMMANDS;
import static krak.miche.KBot.structures.Reminder.*;

/**
 * @author Kraktun
 * @version 1.0
 * Manages updates: distinguish between commands and normal messages/filtered commands
 */

public class CommandsHandler extends TelegramLongPollingCommandBot {

    public static final String LOGTAG = "COMMANDSHANDLER";
    private static volatile CommandsHandler instance;

    public CommandsHandler(DefaultBotOptions options, boolean allowCommandsWithUsername, String botUsername) {
        super( options, allowCommandsWithUsername, botUsername );
        //Bot is disabled: ignore command
        if (UtilsMain.shutdownBots)
            return;
        register(new HelloCommand());
        register(new StartCommand());
        register(new StopCommand());
        register(new StatusCommand());
        register(new InfoCommand());
        register(new BlacklistCommand());
        register(new ExportCommand());
        register(new setUserCommand());
        //register(new InitializeCommand());
        //register(new ShutdownCommand());
        register(new removeUserCommand());
        register(new ListUserCommand());
        register(new FeedbackCommand());
        register(new RetrieveFeedbackCommand());
        register(new ClearFeedbackCommand());
        register(new satoolsCommand());
        register(new CustomSQLCommand());
        register(new TestCommand());
        register(new AntifloodCommand());
        register(new ReminderCommand());
        register(new GetAdminsCommand());
        register(new SettingsCommand());
        register(new LanguageCommand());
        register(new UpdateUsernameCommand());
        register(new SetUTCCommand());
        register(new LogCommand());
        register(new ExportLogCommand());
        register(new ClearLogCommand());
        HelpCommand helpCommand = new HelpCommand(this);
        register(helpCommand);

        registerDefaultAction(( absSender, message )->{
            if (message.getChat().isUserChat())
            {
                SendMessage commandUnknownMessage = new SendMessage();
                commandUnknownMessage.setChatId(message.getChatId());
                commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot. Type /help for help ");
                try {
                    absSender.execute(commandUnknownMessage);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        });
        instance = this;
    }


    //Bot must be admin in a group to work properly
    @Override
    protected boolean filter(Message message) {
        if (message.isGroupMessage())
        {
            String messageText = message.getText();
            if (isReplyCommand(messageText))
                return true;
        }
        return false;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        //Bot is disabled: ignore message
        if (UtilsMain.shutdownBots)
            return;
        if (update.hasMessage())
        {
            if (ignoreOldUpdates && update.getMessage().getDate() < UtilsMain.getStartTime())
                return;
            Message message = update.getMessage();
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            int userID = message.getFrom().getId();
            SendMessage echoMessage = new SendMessage();
            echoMessage.setChatId(message.getChatId());
            ReplyKeyboardRemove deleteKey;
            boolean botIgnore = false; //used to check what the bot should control

            if (message.isUserMessage())
            {
                String language = databaseManager.getUserLanguage(update.getMessage().getFrom().getId());
                if (!databaseManager.isDBUser(userID) || databaseManager.isRemovedStatus(userID))
                {
                    echoMessage.setText(Localizer.getString("restart_bot", language));
                    try {
                        execute(echoMessage);
                    } catch (TelegramApiException e) {
                        BotLogger.error(LOGTAG, e);
                    }
                    return;
                }
                if (message.hasText() && databaseManager.isDBCommandUser(userID))
                {
                    String lastcommand = databaseManager.getLastCommand(userID);
                    boolean destroyUserFromTable = true; //REMOVE USER FROM COMMAND TABLE
                    String command;
                    switch (lastcommand)
                    {
                    case "feedback":
                        String feedback = message.getText();
                        int oldFeedbackLength = 0;
                        try {
                            oldFeedbackLength = databaseManager.getFeedbacklength(userID);
                        } catch (SQLException e) {
                            BotLogger.error(LOGTAG, e);
                        }
                        if (feedback.length() + oldFeedbackLength + MAX_ADDED_FEEDBACK >= MAX_FEEDBACK_LENGTH)
                        {
                            echoMessage.setText(Localizer.getString("exceeding_feedback", language));
                        }
                        else
                        {
                            try {
                                databaseManager.insertFeedback(userID, feedback);
                                echoMessage.setText(Localizer.getString("feedback_sent", language));
                            } catch (SQLException e) {
                                echoMessage.setText(Localizer.getString("error_sending_feedback", language));
                                BotLogger.error(LOGTAG, e);
                            }
                        }
                        break;
                    //Order of commands here must be done so that the check for a command A is done before the command B that actually
                    // push A command in the table.
                    //So you must not put changestatusID before changestatusSTATUS because changestatusID updates "lastcommand"
                    //value to changestatusSTATUS and changestatusSTATUS is performed exactly after ID without waiting for user input
                    case "removeuser": {
                        String userINT = message.getText();
                        echoMessage.setText(RemoveUserHandler.removeUser(userINT, language).toString());
                        break;
                    }
                    case "changestatusSTATUS":
                        String userSTATUS = message.getText();
                        String userCommandID = databaseManager.getCommandParam1(userID);
                        echoMessage.setText(ChangeStatusHandler.updateStatus(userCommandID, userSTATUS, language).toString());
                        break;
                    case "changestatusID": {
                        String userINT = message.getText();
                        try {
                            databaseManager.pushCommand(userID, "changestatusSTATUS");
                            databaseManager.insertCommandParam1(userID, userINT);
                            echoMessage.setText(Localizer.getString("type_status", language));
                            destroyUserFromTable = false;
                        } catch (SQLException e) {
                            BotLogger.error(LOGTAG, e);
                            echoMessage.setText(Localizer.getString("error_changeSTATUS", language));
                        }
                        break;
                    }
                    case "blacklistID": {
                        String userINT = message.getText();
                        echoMessage.setText(BlacklistHandler.blacklist(userINT, language).toString());
                        break;
                    }
                    case "remindmeMessage":
                        String reminderMessage = message.getText();
                        String reminderT = databaseManager.getCommandParam1(userID);
                        String isTimerC = databaseManager.getCommandParam2(userID);
                        ReminderHandler reminderHandler = ReminderHandler.getInstance();
                        boolean isTimer = isTimerC.equalsIgnoreCase("t");
                        if (reminderHandler.addReminder(userID + "", reminderMessage, reminderT, isTimer))
                            echoMessage.setText(Localizer.getString("done", language));
                        else
                            echoMessage.setText(Localizer.getString("bot_shutting_down", language));
                        break;
                    case "remindmeTime": {
                        String reminderTime = message.getText();
                        try {
                            if (getParamNum(reminderTime) >= 2)
                            {
                                Scanner reminderscan = new Scanner(reminderTime);
                                String isTimerT = reminderscan.next();
                                reminderTime = reminderscan.next();
                                reminderscan.close();
                                if (isFormattedReminder(isTimerT, reminderTime))
                                {
                                    echoMessage.setText(Localizer.getString("type_message_reminder", language));
                                    databaseManager.pushCommand(userID, "remindmeMessage");
                                    databaseManager.insertCommandParam1(userID, reminderTime);
                                    databaseManager.insertCommandParam2(userID, isTimerT);
                                    destroyUserFromTable = false;
                                }
                                else
                                    echoMessage.setText(Localizer.getString("invalid_input_time", language));
                            }
                            else
                                echoMessage.setText(Localizer.getString("invalid_input", language));
                        } catch (SQLException e) {
                            BotLogger.error(LOGTAG, e);
                            echoMessage.setText(Localizer.getString("error_remindmeMessage", language));
                        }
                        break;
                    }
                    case "satools":
                        command = message.getText();
                        switch (command)
                        {
                        case "List Groups":
                            StringBuilder t1 = ListGroupsHandler.getGroups();
                            echoMessage.setText(t1.toString());
                            break;
                        case "Export":
                            StringBuilder t2 = ExportHandler.write();
                            echoMessage.setText(t2.toString());
                            break;
                        case "List Users":
                            StringBuilder t3 = ListUsersHandler.getUsers();
                            echoMessage.setText(t3.toString());
                            break;
                        case "Remove User":
                            try {
                                databaseManager.pushCommand(userID, "removeuser");
                                echoMessage.setText(Localizer.getString("type_id_user_remove", language));
                                destroyUserFromTable = false;
                            } catch (SQLException e) {
                                BotLogger.error(LOGTAG, e);
                                echoMessage.setText(Localizer.getString("error_removeuser", language));
                            }
                            break;
                        case "Change Status":
                            try {
                                databaseManager.pushCommand(userID, "changestatusID");
                                echoMessage.setText(Localizer.getString("type_ID_user", language));
                                destroyUserFromTable = false;
                            } catch (SQLException e) {
                                BotLogger.error(LOGTAG, e);
                                echoMessage.setText(Localizer.getString("error_changestatusID", language));
                            }
                            break;
                        case "Blacklist User":
                            try {
                                databaseManager.pushCommand(userID, "blacklistID");
                                echoMessage.setText(Localizer.getString("type_ID_user", language));
                                destroyUserFromTable = false;
                            } catch (SQLException e) {
                                BotLogger.error(LOGTAG, e);
                                echoMessage.setText(Localizer.getString("error_blacklistID", language));
                            }
                            break;
                        case "Get feedback":
                            StringBuilder feed = RetrieveFeedbackHandler.getFeedbacks();
                            echoMessage.setText(feed.toString());
                            break;
                        case "Clear Feedback":
                            StringBuilder feed2 = ClearFeedbackHandler.clearFeedbacks();
                            echoMessage.setText(feed2.toString());
                            break;
                        case "Shutdown":
                            try {
                                ShutdownHandler.preOFF(userID);
                                StringBuilder shut = ShutdownHandler.poweroff();
                                echoMessage.setText(shut.toString());
                            } catch (SQLException e) {
                                echoMessage.setText(Localizer.getString("error_shutdown_remove", language));
                            }
                            destroyUserFromTable = false;
                            break;

                        default:
                            echoMessage.setText(Localizer.getString("something_wrong", language));
                            break;
                        }
                        deleteKey = new ReplyKeyboardRemove();
                        echoMessage.setReplyMarkup(deleteKey);
                        break;
                    case "settingsLanguage":
                        command = message.getText();
                        switch (command)
                        {
                        case "English":
                            try {
                                databaseManager.changeUserLanguage(userID, "en");
                                echoMessage.setText(Localizer.getString("OK", language));
                            } catch (SQLException e) {
                                echoMessage.setText(Localizer.getString("something_wrong", language));
                            }
                            break;
                        case "Italiano":
                            try {
                                databaseManager.changeUserLanguage(userID, "it");
                                echoMessage.setText(Localizer.getString("OK", language));
                            } catch (SQLException e) {
                                echoMessage.setText(Localizer.getString("something_wrong", language));
                            }
                            break;
                        default:
                            echoMessage.setText(Localizer.getString("what", language));
                            break;
                        }
                        deleteKey = new ReplyKeyboardRemove();
                        echoMessage.setReplyMarkup(deleteKey);
                        break;
                    case "settingsUTC":
                        String utcTime = paramCommand(message.getText(), 1)[0];
                        if (utcTime == null)
                            echoMessage.setText(Localizer.getString("invalid_input", language));
                        else
                            echoMessage.setText(SetUTCHandler.insertUTC(false, utcTime, userID).toString());
                        break;
                    case "settings":
                        command = message.getText();
                        switch (command)
                        {
                        case "Language":
                            echoMessage.setText(Localizer.getString("choose_language", language));
                            ReplyKeyboardMarkup replyKeyboardMarkup = SettingsKeyboard.sendLanguageKeyboard();
                            echoMessage.setReplyMarkup(replyKeyboardMarkup);
                            try {
                                databaseManager.pushCommand(userID, "settingsLanguage");
                            } catch (SQLException e) {
                                echoMessage.setText(Localizer.getString("something_wrong", language));
                            }
                            destroyUserFromTable = false;
                            break;
                        case "Set UTC":
                            echoMessage.setText(Localizer.getString("set_UTC", language) + DEFAULT_UTC);
                            try {
                                databaseManager.pushCommand(userID, "settingsUTC");
                            } catch (SQLException e) {
                                echoMessage.setText(Localizer.getString("something_wrong", language));
                            }
                            deleteKey = new ReplyKeyboardRemove();
                            echoMessage.setReplyMarkup(deleteKey);
                            destroyUserFromTable = false;
                            break;
                        default:
                            echoMessage.setText(Localizer.getString("what", language));
                            deleteKey = new ReplyKeyboardRemove();
                            echoMessage.setReplyMarkup(deleteKey);
                            break;
                        }
                        break;
                    }
                    if (destroyUserFromTable)
                    {
                        try {
                            databaseManager.removeUserCommand(userID);
                        } catch (SQLException e) {
                            echoMessage.setText(Localizer.getString("error_removing_commandsTable", language));
                            BotLogger.error(LOGTAG, e);
                        }
                    }
                    try {
                        execute(echoMessage);
                    } catch (TelegramApiException e) {
                        BotLogger.error(LOGTAG, e);
                    }
                }
                else if (message.hasText() && !message.isCommand())
                {
                    echoMessage.setText(Localizer.getString("no_command", language));
                    try {
                        execute(echoMessage);
                    } catch (TelegramApiException e) {
                        BotLogger.error(LOGTAG, e);
                    }
                }
            }
            else if (message.isGroupMessage())
            {
                String language = databaseManager.getGroupLanguage(update.getMessage().getChatId());
                if (message.hasText() && isReplyCommand(firstCommand(message.getText())))
                {
                    StringBuilder rispondi = new StringBuilder(Localizer.getString("error_internal", language));
                    String command = firstCommand(message.getText());
                    if (message.isReply())
                    {
                        Message replyMSG;
                        replyMSG = message.getReplyToMessage();
                        switch (command)
                        {
                        case "/kick": case "/kick@" + BOT_USERNAME:
                            if (( !SUPER_ADMINS.contains(replyMSG.getFrom().getId()) && !databaseManager.isUserGroupAdmin(message.getChatId(), message.getFrom().getId()) ) && ( SUPER_ADMINS.contains(message.getFrom().getId()) || databaseManager.isUserGroupAdmin(message.getChatId(), message.getFrom().getId()) ))
                            {
                                rispondi = KickHandler.kickUser(replyMSG.getFrom(), message.getChat(), this);
                            }
                            else
                                rispondi = new StringBuilder(Localizer.getString("you_cant_do_this", language));
                            break;
                        case "/setstatus": case "/setstatus@" + BOT_USERNAME:
                            if (getParamNum(message.getText()) >= 2 && ( SUPER_ADMINS.contains(message.getFrom().getId()) || databaseManager.isUserGroupAdmin(message.getChatId(), message.getFrom().getId()) ))
                            {
                                String param1 = paramCommand(message.getText(), 2)[1];     //[0] is the command
                                SetUserGroupHandler setuser = new SetUserGroupHandler();
                                rispondi = setuser.updateStatus(message.getChatId(), replyMSG.getFrom(), param1);
                            }
                            else
                                rispondi = new StringBuilder(Localizer.getString("syntax_error", language));
                            break;
                        case "/ban": case "/ban@" + BOT_USERNAME:
                            if (( !SUPER_ADMINS.contains(replyMSG.getFrom().getId()) && !databaseManager.isUserGroupAdmin(message.getChatId(), message.getFrom().getId()) ) && ( SUPER_ADMINS.contains(message.getFrom().getId()) || databaseManager.isUserGroupAdmin(message.getChatId(), message.getFrom().getId()) ))
                            {
                                rispondi = KickHandler.kickUser(replyMSG.getFrom(), message.getChat(), this);
                                SetUserGroupHandler setuser = new SetUserGroupHandler();
                                rispondi.append("\n");
                                rispondi.append(setuser.updateStatus(message.getChatId(), replyMSG.getFrom(), BLACKLISTED_STATUS).toString());
                            }
                            else
                                rispondi = new StringBuilder(Localizer.getString("you_cant_do_this", language));
                            break;
                        case "/unban": case "/unban@" + BOT_USERNAME:
                            if (( !SUPER_ADMINS.contains(replyMSG.getFrom().getId()) && !databaseManager.isUserGroupAdmin(message.getChatId(), message.getFrom().getId()) ) && ( SUPER_ADMINS.contains(message.getFrom().getId()) || databaseManager.isUserGroupAdmin(message.getChatId(), message.getFrom().getId()) ))
                            {
                                SetUserGroupHandler setuser = new SetUserGroupHandler();
                                rispondi = setuser.updateStatus(message.getChatId(), replyMSG.getFrom(), USER_STATUS);
                            }
                            else
                                rispondi = new StringBuilder(Localizer.getString("you_cant_do_this", language));
                            break;
                        }
                    }
                    else if (command.equals("/unban") || command.equals("/unban@" + BOT_USERNAME))
                    {
                        if (getParamNum(message.getText()) >= 2)
                        {
                            if (( SUPER_ADMINS.contains(message.getFrom().getId()) || databaseManager.isUserGroupAdmin(message.getChatId(), message.getFrom().getId()) ))
                            {
                                String param1 = paramCommand(message.getText(), 2)[1]; //[0] is the command
                                try {
                                    int userInt = Integer.parseInt(param1);
                                    SetUserGroupHandler setuser = new SetUserGroupHandler();
                                    rispondi = setuser.updateStatus(message.getChatId(), userInt, param1);
                                } catch (NumberFormatException e) {
                                    rispondi = new StringBuilder(Localizer.getString("error", language));
                                }
                            }
                            else
                                rispondi = new StringBuilder(Localizer.getString("not_allowed", language));
                        }
                        else
                            rispondi = new StringBuilder(Localizer.getString("syntax_error", language));
                    }
                    else
                    {
                        rispondi = new StringBuilder(Localizer.getString("syntax_error", language));
                    }
                    echoMessage.setText(rispondi.toString());
                    try {
                        execute(echoMessage);
                    } catch (TelegramApiException e) {
                        BotLogger.error(LOGTAG, e);
                    }
                }
                else if (message.getNewChatMembers() != null)
                {
                    StringBuilder rispondi = new StringBuilder(Localizer.getString("error_internal", language));
                    List<User> newUser = message.getNewChatMembers();
                    for (User utente : newUser)
                    {
                        if (utente.getId()!=BOT_ID) {
                            rispondi = new StringBuilder(Localizer.getString("new_user", language) + getFormattedUsername(utente));
                            if (CheckStatusHandler.isBlacklisted(message.getChatId(), utente.getId())) {
                                KickHandler.kickUser(utente, message.getChat(), this);
                                rispondi.append("\n");
                                rispondi.append(Localizer.getString("new_user_removed", language));
                            }
                        }
                        else {
                            botIgnore = true;
                            if (!databaseManager.isDBGroup(message.getChatId())) {
                                try {
                                    databaseManager.addGroup(message.getChatId());
                                    InitializeHandler.startUserGroupTable(message.getChatId());
                                    InitializeHandler.startGroupSettings(message.getChatId());
                                    InitializeHandler.addAdminsGroup(this, message.getChatId(), BOT_ID);
                                    rispondi = new StringBuilder(Localizer.getString("group_added", language));
                                    rispondi.append("\n");
                                    rispondi.append(Localizer.getString("group_added_plus", language));
                                    rispondi.append("\n");
                                    rispondi.append(Localizer.getString("group_added_plus2", language));
                                } catch (SQLException e) {
                                    BotLogger.error(LOGTAG, e);
                                    rispondi.append(Localizer.getString("error_adding_group", language));
                                }
                                break;
                            }
                            else {
                                rispondi = new StringBuilder(Localizer.getString("group_added", language));
                                rispondi.append("\n");
                                rispondi.append(Localizer.getString("group_added_plus", language));
                                rispondi.append("\n");
                                rispondi.append(Localizer.getString("group_added_plus2", language));
                            }
                        }
                    }
                    echoMessage.setText(rispondi.toString());
                    echoMessage.enableHtml(true);
                    try {
                        execute(echoMessage);
                    } catch (TelegramApiException e) {
                        BotLogger.error(LOGTAG, e);
                    }
                }
                else if (message.getLeftChatMember() != null)
                {
                    //To be sure I use both username and id
                    if (message.getFrom().getUserName().equals(BOT_USERNAME) || message.getFrom().getId() == BOT_ID)
                    {
                        try {
                            databaseManager.removeGroup(message.getChatId());
                        } catch (SQLException e)   {
                            BotLogger.error(LOGTAG, e);
                        }
                    }
                    else
                    {
                        User removedUser = message.getLeftChatMember();
                        if (databaseManager.isUserGroupAdmin(message.getChatId(), removedUser.getId()))
                        {
                            StringBuilder rispondi;
                            String username = getFormattedUsername(removedUser);
                            rispondi = new StringBuilder(Localizer.getString("User", language))
                                       .append(username).append(Localizer.getString("removed_admin_table", language));
                            SetUserGroupHandler setuser = new SetUserGroupHandler();
                            setuser.updateStatus(message.getChatId(), removedUser, USER_STATUS);
                            echoMessage.setText(rispondi.toString());
                            try {
                                execute(echoMessage);
                            } catch (TelegramApiException e) {
                                BotLogger.error(LOGTAG, e);
                            }
                        }
                    }
                }
                if (!botIgnore && message.hasText() && databaseManager.isDBCommandUser(userID))
                {
                    String lastcommand = databaseManager.getLastCommand(userID);
                    boolean destroyUserFromTable = true; //REMOVE USER FROM COMMAND TABLE
                    switch (lastcommand)
                    {
                        case "remindmeGroupMessage":
                            String reminderMessage = message.getText();
                            String reminderT = databaseManager.getCommandParam1(userID);
                            String isTimerC = databaseManager.getCommandParam2(userID);
                            ReminderHandler reminderHandler = ReminderHandler.getInstance();
                            boolean isTimer = isTimerC.equalsIgnoreCase("t");
                            if (reminderHandler.addReminder("G" + SQLUtil.longtoString(message.getChatId()), reminderMessage, reminderT, isTimer))
                                echoMessage.setText(Localizer.getString("done", language));
                            else
                                echoMessage.setText(Localizer.getString("bot_shutting_down", language));
                            break;
                        case "remindmeGroupTime": {
                            String reminderTime = message.getText();
                            try {
                                if (getParamNum(reminderTime) >= 2)
                                {
                                    Scanner reminderscan = new Scanner(reminderTime);
                                    String isTimerT = reminderscan.next();
                                    reminderTime = reminderscan.next();
                                    reminderscan.close();
                                    if (isFormattedReminder(isTimerT, reminderTime))
                                    {
                                        echoMessage.setText(Localizer.getString("type_message_reminder", language));
                                        databaseManager.pushCommand(userID, "remindmeGroupMessage");
                                        databaseManager.insertCommandParam1(userID, reminderTime);
                                        databaseManager.insertCommandParam2(userID, isTimerT);
                                        destroyUserFromTable = false;
                                    }
                                    else
                                        echoMessage.setText(Localizer.getString("invalid_input_time", language));
                                }
                                else
                                    echoMessage.setText(Localizer.getString("invalid_input", language));
                            } catch (SQLException e) {
                                BotLogger.error(LOGTAG, e);
                                echoMessage.setText(Localizer.getString("error_remindmeMessage", language));
                            }
                            break;
                        }
                    }
                    if (destroyUserFromTable)
                    {
                        try {
                            databaseManager.removeUserCommand(userID);
                        } catch (SQLException e) {
                            echoMessage.setText(Localizer.getString("error_removing_commandsTable", language));
                            BotLogger.error(LOGTAG, e);
                        }
                    }
                    try {
                        execute(echoMessage);
                    } catch (TelegramApiException e) {
                        BotLogger.error(LOGTAG, e);
                    }
                }
                /*
                Rather than make a query on the DB every time (quite slow) It would be better to store groups with antiflood and logging
                enabled in a list somewhere and update them on boot and when necessary.
                 */
                //ANTIFLOOD
                if (!botIgnore && databaseManager.isAntifloodActive(message.getChatId()))
                {
                    int user = message.getFrom().getId();
                    long chatID = message.getChatId();
                    AntiFloodHandler antiflood = AntiFloodHandler.getInstance();
                    antiflood.setAntiflood(chatID, "start"); //If bot is restarted you need to reenable antiflood for selected group
                    boolean lincia = antiflood.addUserMessage(chatID, user);
                    StringBuilder risposta;
                    if (lincia)
                    {
                        if (!SUPER_ADMINS.contains(user) && !databaseManager.isUserGroupAdmin(chatID, user ))
                        {
                            risposta = KickHandler.kickUser(message.getFrom(), message.getChat(), this);
                            risposta.append("\n");
                            risposta.append(Localizer.getString("by_antiflood", language));
                            SendMessage riss = new SendMessage();
                            riss.setChatId(chatID);
                            riss.setText(risposta.toString());
                            try {
                                execute(riss);
                            } catch (TelegramApiException e) {
                                BotLogger.error(LOGTAG, e);
                            }
                        }
                    }
                }
                //LOG
                if (!botIgnore && databaseManager.isLogActive(message.getChatId())){
                    boolean confirmed = true;
                    if (message.hasText()) {
                        if (LogHandler.isSpecialMessage(message) && databaseManager.isGroupLogDefault(message.getChatId())) {
                            confirmed = LogHandler.logMessage(message, true);
                        } else if (!LogHandler.isSpecialMessage(message) && !databaseManager.isGroupLogDefault(message.getChatId())) {
                            confirmed = LogHandler.logMessage(message, false);
                        }
                    }
                    else if (message.hasPhoto()) {
                        PhotosHandler.getPhotoFromMessage(this, message);
                    }
                    else if (message.hasDocument()) {
                        DocumentsHandler.getDocumentFromMessage(this, message);
                    }
                    if (!confirmed) {
                        SendMessage riss = new SendMessage();
                        riss.setChatId(message.getChatId());
                        riss.setText(Localizer.getString("error", language));
                        try {
                            execute(riss);
                        } catch (TelegramApiException e) {
                            BotLogger.error(LOGTAG, e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getBotToken() {
        return BotConfig.COMMANDS_TOKEN;
    }


    public static CommandsHandler getInstance() {
        return instance;
    }

    //Normal commands in groups are managed by CommandsHandler, but when they are replies we need the whole update to get
    //both users. That's why we filter this commands and manage them in the processNonCommandUpdate.

    private boolean isReplyCommand(String testo) {
        testo = firstCommand(testo);
        for (CommandObject command : GROUP_COMMANDS)
        {
            if (command.isFiltered())
            {
                if (testo.equals(command.getCommandOnly()) || testo.equals(command.getCommandWithUsername(BOT_USERNAME)))
                    return true;
            }
        }
        return false;
    }
}
