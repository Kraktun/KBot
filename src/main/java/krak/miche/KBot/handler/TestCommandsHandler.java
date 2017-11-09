package krak.miche.KBot.handler;

import krak.miche.KBot.BotConfig;
import krak.miche.KBot.BuildVars;
import krak.miche.KBot.commands.*;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.secondary_Handler.AntiFloodHandler;
import krak.miche.KBot.secondary_Handler.KickHandler;
import krak.miche.KBot.secondary_Handler.LogHandler;
import krak.miche.KBot.secondary_Handler.PhotosHandler;
import krak.miche.KBot.services.Localizer;
import krak.miche.KBot.services.UtilsMain;
import krak.miche.KBot.structures.CommandObject;
import krak.miche.KBot.structures.CommandsStatic;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.*;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.SQLException;

import static krak.miche.KBot.BuildVars.BOT_TEST_USERNAME;
import static krak.miche.KBot.BuildVars.SUPER_ADMINS;

public class TestCommandsHandler extends TelegramLongPollingCommandBot {

    public static final String LOGTAG = "COMMANDSHANDLER";
    private static volatile TestCommandsHandler instanceTest;

    public TestCommandsHandler(DefaultBotOptions options, boolean allowCommandsWithUsername, String botUsername) {
        super( options, allowCommandsWithUsername, botUsername );
        //register(new HelloCommand());
        //register(new StartCommand());
        //register(new StopCommand());
        //register(new StatusCommand());
        //register(new InfoCommand());
        //register(new BlacklistCommand());
        //register(new ExportCommand());
        //register(new setUserCommand());
        //register(new InitializeCommand());
        //register(new ShutdownCommand());
        //register(new removeUserCommand());
        //register(new ListUserCommand());
        //register(new FeedbackCommand());
        //register(new RetrieveFeedbackCommand());
        //register(new ClearFeedbackCommand());
        //register(new satoolsCommand());
        //register(new CustomSQLCommand());
        register(new TestCommand());
        //register(new AntifloodCommand());
        //register(new ReminderCommand());
        //register(new GetAdminsCommand());
        //register(new SettingsCommand());
        //register(new LanguageCommand());
        //register(new UpdateUsernameCommand());
        register(new SetUTCCommand());
        register(new LogCommand());
        register(new ExportLogCommand());
        register(new ClearLogCommand());
        HelpCommand helpCommand = new HelpCommand(this);
        register(helpCommand);
        /*
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
        */
        instanceTest = this;
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
        if (update.hasMessage())
        {
            if (BuildVars.ignoreOldUpdates && update.getMessage().getDate() < UtilsMain.getStartTime())
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
                if(databaseManager.isDBCommandUser(userID)){
                    String lastcommand = databaseManager.getLastCommand(userID);
                    boolean destroyUserFromTable = true; //REMOVE USER FROM COMMAND TABLE
                    if (lastcommand.equals("test")){
                        if (message.hasPhoto()) {
                            PhotosHandler.getPhotoFromMessage(this, message);
                        }
                    }
                    if (destroyUserFromTable)
                    {
                        try {
                            databaseManager.removeUserCommand(userID);
                        } catch (SQLException e) {
                            BotLogger.error(LOGTAG, e);
                        }
                    }
                }
            }
            else if (message.isGroupMessage())
            {
                String language = databaseManager.getGroupLanguage(update.getMessage().getChatId());
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
        return BotConfig.TEST_TOKEN;
    }


    public static TelegramLongPollingCommandBot getInstanceTest() {
        return instanceTest;
    }

    //Normal commands in groups are managed by CommandsHandler, but when they are replies we need the whole update to get
    //both users. That's why we filter this commands and manage them in the processNonCommandUpdate.

    private boolean isReplyCommand(String testo) {
        testo = HandlerUtil.firstCommand(testo);
        for (CommandObject command : CommandsStatic.GROUP_COMMANDS)
        {
            if (command.isFiltered())
            {
                if (testo.equals(command.getCommandOnly()) || testo.equals(command.getCommandWithUsername(BOT_TEST_USERNAME)))
                    return true;
            }
        }
        return false;
    }
}

