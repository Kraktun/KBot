package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.SQLUtil;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.objects.Document;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;


public class DocumentsHandler {

    public static final String LOGTAG = "DOCUMENTSHANDLER";
    private static final int MAX_SIZE = 20000000; //20MB

    /**
     * Retrieves document from a message and saves it locally
     * @param telegramLongPollingBot Bot that received the message, necessary to get the URL to download the file
     * @param message message containing the document
     */
    public static void getDocumentFromMessage(TelegramLongPollingCommandBot telegramLongPollingBot, Message message) {
        String fileName;
        if (message.getChat().isGroupChat())
            fileName = "G" + SQLUtil.longtoString(message.getChatId()) + "_";
        else
            fileName = message.getFrom().getId() + "_";
        if (message.hasDocument()) {
            Document document = message.getDocument();
            if (document!=null && document.getFileSize()<MAX_SIZE) { //File size is in byte
                String temp = document.getFileId();
                GetFile getFile = new GetFile();
                getFile = getFile.setFileId(temp);
                fileName = fileName + "_" + document.getFileName(); //already contains extension
                try {
                    File newFile = telegramLongPollingBot.execute(getFile);
                    String url = newFile.getFileUrl(telegramLongPollingBot.getBotToken());
                    FileDownloaderHandler.saveFile(url, fileName);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }
}
