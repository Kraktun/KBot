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

    /**
     * Method run on boot to initialize threads and DB
     */
    public static void onStartJob() {
        START_TIME = Instant.now().getEpochSecond();
        StringBuilder log = new StringBuilder();
        log.append("\n");
        log.append(InitializeHandler.startUsersTable());
        log.append("\n");
        log.append(InitializeHandler.startFeedbackTable());
        log.append("\n");
        log.append(InitializeHandler.startCommandsTable());
        log.append("\n");
        log.append(InitializeHandler.startLogsTable());
        log.append("\n");
        log.append(InitializeHandler.startGroupTable());
        BotLogger.info(LOGTAG, log.toString());
        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            BotLogger.error(LOGTAG, e);
        }
        JobExecutor jobExecutor = JobExecutor.getInstance();
        jobExecutor.run();
    }

    /**
     * Method run on shut down to correctly close DB and threads
     */
    public static void onCloseJob() {
        killBots();
        ShutdownHandler.poweroff();
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

    /**
     * Ignore updates while bot is shutting down
     */
    public static void killBots() {
        shutdownBots = true;
    }

    /**
     * @return time of the bot boot
     */
    public static long getStartTime() {
        return START_TIME;
    }

    /**
     * @param user user to get the formatted username
     * @return username if exists or first + last name or only first name
     */
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

    /**
     * Get path of current jar (from stackoverflow)
     * @param aclass Class to find the path
     * @return path of the jar containing that class
     * @throws Exception if an error occurred
     */
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

    /**
     * Backups users and groups in a text file
     * @param name name of the file
     * @throws Exception if an error occurred
     */
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

    /**
     * Writes a file as a txt file
     * @param name name of the file
     * @param text what to write in the file
     * @param folder folder where to write it (Note: path is th epth of the jar)
     * @return File written
     * @throws Exception if an error occurred
     */
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

    /**
     * Compute delay in seconds between current time and target time, with custom utc
     * @param targetMonth target month
     * @param targetDay target day
     * @param targetHour target hour
     * @param targetMinute target minute
     * @param UTC utc of the target hour
     * @return seconds between current and target date
     */
    public static long computeNextRun(int targetMonth, int targetDay, int targetHour, int targetMinute, int UTC) {
        final LocalDateTime localNow = LocalDateTime.now(Clock.systemUTC());
        LocalDateTime localNextTarget = localNow.withMonth(targetMonth).withDayOfMonth(targetDay).withHour(targetHour).withMinute(targetMinute).withSecond(0);
        if (targetMonth < localNow.getMonthValue())
            localNextTarget = localNextTarget.plusYears(1);
        localNextTarget = localNextTarget.minusHours(UTC);
        final Duration duration = Duration.between(localNow, localNextTarget);
        return duration.getSeconds();

    }

    /**
     * Get formatted time to save in logs
     * @param timeSeconds time as saved in unix
     * @param UTC target utc
     * @return string with formatted date
     */
    public static String getFormattedTime(int timeSeconds, String UTC) {
        final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (Integer.parseInt(UTC)>=0)
            UTC = "+" + UTC;
        final String formattedDtm = Instant.ofEpochSecond(timeSeconds)
                                    .atZone(ZoneId.of("GMT" + UTC))
                                    .format(formatter);
        return formattedDtm;
    }

    /**
     * Custom print message
     * @param print message to prin
     */
    public static void println(String print) {
        System.out.println("TEST: " + print);
    }

    /**
     * Custom print message
     * @param log message to print
     */
    public static void log(String log) {
        System.err.println("LOG: " + log);
    }
}
