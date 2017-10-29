package krak.miche.KBot.secondary_Handler;

import krak.miche.KBot.BuildVars;
import krak.miche.KBot.services.Localizer;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.File;

public class SendFileHandler {

    private static final String LOGTAG = "SENDFILEHANDLER";

    public static void sendFileToChat(Chat chat, AbsSender absSender, File file) {
        SendDocument sendDocument = new SendDocument();
        sendDocument = sendDocument.setChatId(chat.getId()).setNewDocument(file);
        if (file.length()>0) {
            try {
                absSender.sendDocument(sendDocument);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        }
        else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chat.getId());
            sendMessage.setText(Localizer.getString("error", BuildVars.DEFAULT_LANG));
            try {
                absSender.execute(sendMessage);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        }
    }
}
