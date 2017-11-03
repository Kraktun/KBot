package krak.miche.KBot.commands;



import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.secondary_Handler.InitializeHandler;
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
 * This command is no more necessary as of now.
 * But I leave it here if necessary in the future.
 */

public class InitializeCommand extends BotCommand {

    public static final String LOGTAG = "INITIALIZECOMMAND";

    public InitializeCommand() {
        super( "initialize", "With this command you can initialize the database" );
    }

    /*
       This method initializes the user database: if the table with users doesn't exist, it creates it and adds the users in lists
       SUPER_ADMINS, ADMINS, BLACKLISTED... from BuildVars. If they are already present in the database it overwrites their status.
       SUPER_ADMINS must be set in SUPER_ADMINS list.
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        StringBuilder messageTextBuilder;
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language;
        if (chat.isUserChat())
        {
            //INITIALIZE SCHEMAS
            if (BuildVars.SUPER_ADMINS.contains(user.getId()))
            {

                //INITIALIZE USERS TABLE
                messageTextBuilder = InitializeHandler.startUsersTable();
                messageTextBuilder.append("\n");

                //INITIALIZE FEEDBACKS TABLE
                messageTextBuilder.append(InitializeHandler.startFeedbackTable());
                messageTextBuilder.append("\n");

                //INITIALIZE COMMANDS TABLE
                messageTextBuilder.append(InitializeHandler.startCommandsTable());
                messageTextBuilder.append("\n");

                //INITIALIZE GROUPS TABLE
                messageTextBuilder.append(InitializeHandler.startGroupTable());
                messageTextBuilder.append("\n");

                //INITIALIZE LOGS TABLE
                messageTextBuilder.append(InitializeHandler.startLogsTable());
                messageTextBuilder.append("\n");

                SendMessage answer4 = new SendMessage();
                answer4.setChatId(chat.getId().toString());
                answer4.setText(messageTextBuilder.toString());
                try {
                    absSender.execute(answer4);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
        else if (chat.isGroupChat())
        {
            if (BuildVars.SUPER_ADMINS.contains(user.getId()))
            {
                language = databaseManager.getGroupLanguage(chat.getId());
                String message3 = Localizer.getString("only_user_chat", language);
                SendMessage answer3 = new SendMessage();
                answer3.setChatId(chat.getId().toString());
                answer3.setText(message3);
                try {
                    absSender.execute(answer3);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }
}
