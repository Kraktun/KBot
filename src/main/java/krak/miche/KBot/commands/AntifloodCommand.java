package krak.miche.KBot.commands;


import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.secondary_Handler.AntiFloodHandler;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;


import static krak.miche.KBot.BuildVars.*;

/**
 * @author Kraktun
 * @version 1.0
 * Class to manage antiflood command:
 * Checks if params are corrects and then
 * send them to AntifloodHandler
 */

public class AntifloodCommand extends BotCommand {

    private static final String LOGTAG = "ANTIFLOODCOMMAND";

    public AntifloodCommand() {
        super( "antiflood", "Only available in group chats" );
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        StringBuilder rispondi;
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if (chat.isGroupChat())
        {
            String language = databaseManager.getGroupLanguage(chat.getId());
            String command = "";
            int i = 0;
            while (i < 3)
            {
                try {
                    command = command + " " + arguments[i];
                    ++i;
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }
            if (( SUPER_ADMINS.contains(user.getId()) || databaseManager.isUserGroupAdmin(chat.getId(), user.getId()) ))
            {
                if (i >= 2)
                {
                    String param1 = arguments[0];
                    String param2 = arguments[1];
                    try {
                        int messages = Integer.parseInt(param1);
                        int timeslice = Integer.parseInt(param2);
                        if (timeslice % ANTIFLOODTIMESLICEMIN != 0)
                        {
                            rispondi = new StringBuilder(Localizer.getString("antiflood_not_multiple", language));
                            rispondi.append(ANTIFLOODTIMESLICEMIN).append(" ");
                            rispondi.append(Localizer.getString("antiflood_not_multiple_2", language));
                        }
                        else
                        {
                            AntiFloodHandler antiflood = AntiFloodHandler.getInstance();
                            rispondi = antiflood.updateAntiflood(chat.getId(), messages, timeslice);
                        }
                    } catch (NumberFormatException e) {
                        rispondi = new StringBuilder(Localizer.getString("syntax_error", language));
                    } catch (Exception e) {
                        rispondi = new StringBuilder(Localizer.getString("error", language) + e);
                    }
                }
                else if (i == 1)
                {
                    String param1 = arguments[0];
                    if (param1.equals("start") || param1.equals("stop"))
                    {
                        AntiFloodHandler antiflood = AntiFloodHandler.getInstance();
                        rispondi = antiflood.setAntiflood(chat.getId(), param1);
                    }
                    else
                        rispondi = new StringBuilder(Localizer.getString("syntax_error", language));
                }
                else
                    rispondi = new StringBuilder(Localizer.getString("syntax_error", language));
            }
            else
                rispondi = new StringBuilder(Localizer.getString("not_allowed", language));
        }
        else
        {
            String language = databaseManager.getUserLanguage(user.getId());
            rispondi = new StringBuilder(Localizer.getString("only_group_chat", language));
        }
        SendMessage answer = new SendMessage();
        answer.setChatId(chat.getId().toString());
        answer.setText(rispondi.toString());
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
