package krak.miche.KBot.commands;

import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.secondary_Handler.HelpHandler;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * @author Kraktun
 * @version 1.0
 * Get a list of all the commands.
 * The list is different if used in user chat or group chat.
 * Note: some commands are not inserted in the list because are meant
 * to be used only by SUPER_ADMINS
 */

public class HelpCommand extends BotCommand {

    private static final String LOGTAG = "HELPCOMMAND";

    private final ICommandRegistry commandRegistry;

    public HelpCommand(ICommandRegistry commandRegistry) {
        super( "help", "Get all the commands this bot provides" );
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String language = databaseManager.getUserLanguage(user.getId());
        StringBuilder helpMessageBuilder = new StringBuilder(Localizer.getString("error", language));
        if (chat.isUserChat())
        {
            helpMessageBuilder = new StringBuilder("<b>Help</b>\n");
            helpMessageBuilder.append(Localizer.getString("help", language)).append("\n");
            helpMessageBuilder.append(HelpHandler.getHelpUser());
        }
        else if (chat.isGroupChat())
        {
            helpMessageBuilder = new StringBuilder("<b>Help</b>\n");
            helpMessageBuilder.append(Localizer.getString("help", language)).append("\n");
            helpMessageBuilder.append(HelpHandler.getHelpGroup());
        }
        SendMessage helpMessage = new SendMessage();
        helpMessage.setChatId(chat.getId().toString());
        helpMessage.enableHtml(true);
        helpMessage.setText(helpMessageBuilder.toString());
        try {
            absSender.execute(helpMessage);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
