package krak.miche.KBot.secondary_Handler;

import krak.miche.KBot.services.UtilsMain;
import krak.miche.KBot.Main;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static krak.miche.KBot.BuildVars.DEFAULT_UTC;

public class FileDownloaderHandler {

    private static final String LOGTAG = "FILEDOWNLOADERHANDLER";
    private static final String FILES_FOLDER = "/Files/";

    /**
     * Saves file from url
     * @param url url of the file to download
     * @param name name of the file to download and save
     */
    public static void saveFile(String url, String name){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT +" + DEFAULT_UTC));
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(cal.getTime());
        if (name!=null)
            timeStamp = timeStamp + name;
        String path = null;
        try {
            path = UtilsMain.getJarContainingFolder(Main.class);
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
        URL website = null;
        try {
            website = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = null;
        fos = new FileOutputStream(path+ FILES_FOLDER + timeStamp);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
