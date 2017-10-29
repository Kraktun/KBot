package krak.miche.KBot.services;


import krak.miche.KBot.Main;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.secondary_Handler.InitializeHandler;
import krak.miche.KBot.secondary_Handler.ShutdownHandler;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @author Kraktun
 * @version 1.0
 */

public class UtilsMain {

    public static final String LOGTAG = "ONSTARTJOB";
    private static long START_TIME;
    public static boolean shutdownBots = false;

    public static void onStartJob() {
        START_TIME = Instant.now().getEpochSecond();

        InitializeHandler handlerinit = new InitializeHandler();
        StringBuilder log = new StringBuilder();
        log.append("\n");
        log.append(handlerinit.startUsersTable());
        log.append("\n");
        log.append(handlerinit.startFeedbackTable());
        log.append("\n");
        log.append(handlerinit.startCommandsTable());
        log.append("\n");
        log.append(handlerinit.startLogsTable());
        log.append("\n");
        log.append(handlerinit.startGroupTable());
        BotLogger.info(LOGTAG, log.toString());
        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            BotLogger.error(LOGTAG, e);
        }
        JobExecutor jobExecutor = JobExecutor.getInstance();
        jobExecutor.run();
    }

    public static void onCloseJob() {
        killBots();
        ShutdownHandler shutdownHandler = new ShutdownHandler();
        shutdownHandler.poweroff();
        JobExecutor jobExecutor = JobExecutor.getInstance();
        if (!jobExecutor.isShutdown()) {
            boolean returnCode = jobExecutor.shutdown();
            if (returnCode)
                log("App Correctly Terminated");
            else
                log("Error closing app");
        }
        else
            log("App Already Terminated");
    }

    public static void killBots() {
        shutdownBots = true;
    }

    public static long getStartTime() {
        return START_TIME;
    }


    public static String getFormattedUsername(User user) {
        String userName = user.getUserName();
        if (userName == null || userName.isEmpty())
        {
            if (user.getLastName() == null)
                userName = user.getFirstName();
            else
                userName = user.getFirstName() + " " + user.getLastName();
        }
        else
            userName = "@" + userName;
        return userName;
    }

    public static String getJarContainingFolder(Class aclass) throws Exception {
        CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();

        File jarFile;

        if (codeSource.getLocation() != null)
        {
            jarFile = new File(codeSource.getLocation().toURI());
        }
        else
        {
            String path = aclass.getResource(aclass.getSimpleName() + ".class").getPath();
            String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
            jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
            jarFile = new File(jarFilePath);
        }
        return jarFile.getParentFile().getAbsolutePath();
    }


    public static void writeUsers(String name) throws Exception {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String path;
        FileWriter filew;
        path = getJarContainingFolder(Main.class);
        path = path + "/Backup/";
        File writerUsers   = new File(path + name + "_Users.txt");
        filew = new FileWriter(writerUsers.getAbsoluteFile());
        BufferedWriter buffw = new BufferedWriter(filew);
        buffw.write(databaseManager.getAllUsers());
        buffw.close();
        filew.close();
        File writerGroups   = new File(path + name + "_Groups.txt");
        filew = new FileWriter(writerGroups.getAbsoluteFile());
        BufferedWriter buffw2 = new BufferedWriter(filew);
        buffw2.write(databaseManager.getAllGroups());
        buffw2.close();
        filew.close();

    }

    public static File writeFile(String name, String text, String folder) throws Exception {
        String path;
        FileWriter filew;
        path = getJarContainingFolder(Main.class);
        path = path + folder;
        File writerUsers   = new File(path + name + ".txt");
        filew = new FileWriter(writerUsers.getAbsoluteFile());
        BufferedWriter buffw = new BufferedWriter(filew);
        buffw.write(text);
        buffw.close();
        filew.close();
        return  writerUsers;
    }

    public static long computeNextRun(int targetMonth, int targetDay, int targetHour, int targetMinute, int UTC) {
        final LocalDateTime localNow = LocalDateTime.now(Clock.systemUTC());
        LocalDateTime localNextTarget = localNow.withMonth(targetMonth).withDayOfMonth(targetDay).withHour(targetHour).withMinute(targetMinute).withSecond(0);
        if (targetMonth < localNow.getMonthValue())
            localNextTarget = localNextTarget.plusYears(1);
        localNextTarget = localNextTarget.minusHours(UTC);
        final Duration duration = Duration.between(localNow, localNextTarget);
        return duration.getSeconds();

    }

    public static String getFormattedTime(int timeSeconds) {
        final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String formattedDtm = Instant.ofEpochSecond(timeSeconds)
                                    .atZone(ZoneId.of("GMT+2"))
                                    .format(formatter);
        return formattedDtm;
    }

    public static void println(String print) {
        System.out.println("TEST: " + print);
    }

    public static void log(String log) {
        System.err.println("LOG: " + log);
    }
}
