package krak.miche.KBot.secondary_Handler;

import krak.miche.KBot.database.SQLUtil;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.UtilsMain;
import krak.miche.KBot.structures.LogObject;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Kraktun
 * @version 1.0
 */

public class ExportLogHandler {
    private static final String LOGTAG = "EXPORTLOGHANDLER";
    private static final String folder = "/Logs/";

    public ExportLogHandler() {
    }

    public static File write(long groupID) {
        String group = "G" + SQLUtil.longtoString(groupID);
        File file = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        try {
            file = UtilsMain.writeFile(group + "_" + timeStamp, extractLogs(groupID), folder);
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
        return file;
    }

    public static File write(long groupID, String name) {
        String group = "G" + SQLUtil.longtoString(groupID);
        File file = null;
        try {
            file = UtilsMain.writeFile(group + "_" + name, extractLogs(groupID), folder);
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
        return file;
    }

    private static String extractLogs(long groupID) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        ArrayList<LogObject> listLog;
        StringBuilder fin = new StringBuilder();
        try {
            listLog = databaseManager.getLogForGroup(groupID);
            for (LogObject log : listLog)
            {
                fin.append(log.getUsername().toUpperCase()).append(" ").append(log.getDate()).append("\n");
                fin.append(log.getMessage()).append("\n");
            }
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return fin.toString();
    }
}
