package krak.miche.KBot.commands;

import krak.miche.KBot.database.DatabaseManager;
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
 * Get chat id
 */

public class InfoCommand extends BotCommand {

    private static final String LOGTAG = "INFOCOMMAND";

    public InfoCommand() {
        super( "info", "This command returns the chat ID" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long chatID = chat.getId();
        String id = chatID.toString();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        StringBuilder messageTextBuilder = new StringBuilder(Localizer.getString("chat_id", language));
        messageTextBuilder.append("\n").append(id);
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
