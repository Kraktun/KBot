package krak.miche.KBot.commands;

import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.secondary_Handler.SetUTCHandler;
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
 * Used to set the correct UTC. This is very important for reminders to work.
 */

public class SetUTCCommand extends BotCommand {

    private static final String LOGTAG = "SETUTCCOMMAND";

    public SetUTCCommand() {
        super( "setutc", "This command is used to set your UTC (default +2)" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        if (chat.isUserChat() || chat.isGroupChat())
        {
            boolean isGroup = false;
            if (chat.isGroupChat())
                isGroup = true;
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            String language = databaseManager.getUserLanguage(user.getId());
            StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("syntax_error", language));
            if (arguments.length > 0)
            {
                messageTextBuilder = SetUTCHandler.insertUTC(isGroup, arguments[0], chat.getId());
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
