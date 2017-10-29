package krak.miche.KBot.commands;

import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;
import krak.miche.KBot.services.UtilsMain;
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
 * A simple test class to say hello to the user
 */

public class HelloCommand extends BotCommand {

    private static final String LOGTAG = "HELLOCOMMAND";

    public HelloCommand() {
        super( "hello", "Say hello to this bot" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        if (chat.isUserChat())
        {
            String userName = UtilsMain.getFormattedUsername(user);
            StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("hello", language)).append(userName);
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
            String userName = UtilsMain.getFormattedUsername(user);
            StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("hello", language)).append(userName);
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
