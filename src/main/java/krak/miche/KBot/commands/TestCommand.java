package krak.miche.KBot.commands;


import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;


import java.sql.SQLException;

/**
 * @author Kraktun
 * @version 1.0
 * Test class for different portions of code
 */

public class TestCommand extends BotCommand {

    private static final String LOGTAG = "TESTCOMMAND";

    public TestCommand() {
        super( "test", "This command is used to test" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Integer userID = user.getId();
        StringBuilder messageTextBuilder;
        if (chat.isUserChat())
        {
            if (BuildVars.SUPER_ADMINS.contains(userID))
            {
                DatabaseManager databaseManager = DatabaseManager.getInstance();
                messageTextBuilder = new StringBuilder("Send in image");
                try {
                    databaseManager.pushCommand(userID, "test");
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
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
    }
}
