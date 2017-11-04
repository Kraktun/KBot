package krak.miche.KBot.secondary_Handler;

import krak.miche.KBot.database.SQLUtil;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.List;

/**
 * @author Kraktun
 * @version 1.0
 */
public class PhotosHandler {

    public static final String LOGTAG = "PHOTOSHANDLER";
    private static final String IMG_EXTENSION = ".jpg";

    /**
     * Download photo from message containing photo and save locally
     * Note: only biggest photo is saved
     * @param telegramLongPollingBot Bot that received the update (necessary to get the url)
     * @param message message containing the photo
     */
    public static void getPhotoFromMessage(TelegramLongPollingCommandBot telegramLongPollingBot, Message message) {
        String fileName;
        if (message.getChat().isGroupChat())
            fileName = "IMG_G" + SQLUtil.longtoString(message.getChatId()) + "_";
        else
            fileName = "IMG_" + message.getFrom().getId() + "_";
        if (message.hasPhoto()) {
            List<PhotoSize> photoSizeList = message.getPhoto();
            if (photoSizeList.size() > 0) {
                PhotoSize photo = photoSizeList.get(photoSizeList.size() - 1); //Save only biggest photo
                String temp = photo.getFileId();
                GetFile getFile = new GetFile();
                getFile = getFile.setFileId(temp);
                try {
                    File newFile = telegramLongPollingBot.execute(getFile);
                    String url = newFile.getFileUrl(telegramLongPollingBot.getBotToken());
                    FileDownloaderHandler.saveFile(url, fileName + IMG_EXTENSION);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }
}
