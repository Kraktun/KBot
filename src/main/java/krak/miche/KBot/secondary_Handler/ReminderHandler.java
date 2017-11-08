package krak.miche.KBot.secondary_Handler;

import krak.miche.KBot.BuildVars;
import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.database.SQLUtil;
import krak.miche.KBot.handler.CommandsHandler;
import krak.miche.KBot.services.Localizer;
import krak.miche.KBot.services.UtilsMain;
import krak.miche.KBot.structures.Reminder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

import org.quartz.JobExecutionException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;


//Still WIP
/**
 * @author Kraktun
 * @version 1.0
 */
public class ReminderHandler {

    public static final String LOGTAG = "REMINDERHANDLER";
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private boolean hasReminders = false;
    private boolean isShuttingDown = false;
    private ArrayList<ReminderObject> reminders;
    private ReentrantLock lock = new ReentrantLock();
    private static volatile ReminderHandler instance;

    /**
     * Private constructor
     * Initialize the list for reminders
     * and restores those in DB
     */
    private ReminderHandler() {
        isShuttingDown = false;
        reminders = new ArrayList<>();
        restoreReminders();
    }

    /**
     * @return instance of the class
     */
    public static ReminderHandler getInstance() {
        final ReminderHandler currentInstance;
        if (instance == null)
        {
            synchronized (ReminderHandler.class) {
                if (instance == null)
                {
                    instance = new ReminderHandler();
                }
                currentInstance = instance;
            }
        }
        else
        {
            currentInstance = instance;
        }
        return currentInstance;
    }

    /**
     * Adds a reminder
     * Note: date check must be done before this point (or it messes up with restore from DB)
     * @param user id of the user / group (groups id must be in form 'G229992922')
     * @param message message of the reminder
     * @param time time when the reminders should be sent
     * @param isTimer true if 'time' is a count down, false if it's a date
     * @return true if you can insert the reminder, false if bot is shutting down
     */
    public boolean addReminder(String user, String message, String time, boolean isTimer) {
        //time must be in form dd.hh.mm.ss days, hours, minutes, seconds
        //or mm.dd.hh.mm
        int utc = BuildVars.DEFAULT_UTC;
        String language;
        try {
            if (user.substring(0,1).equals("G"))
            {
                long usercode = Math.negateExact(Long.valueOf(user.substring(1, user.length())));
                utc = Integer.parseInt(databaseManager.getGroupUTC(usercode));
                language = databaseManager.getGroupLanguage(Long.valueOf(user.substring(1, user.length())));
            }
            else
            {
                utc = databaseManager.getUserUTC(Integer.parseInt(user));
                language = databaseManager.getUserLanguage(Integer.parseInt(user));
            }
        } catch (Exception e) {
            language = BuildVars.DEFAULT_LANG;
        }
        lock.lock();
        try {
            if (isShuttingDown)
            {
                return false;
            }
            if (!hasReminders)
                hasReminders = true;
            long delay;
            if (isTimer)
                delay = computeTimer(time);
            else
                delay = computeDate(time, utc);
            if (delay < 0)
            {
                message = Localizer.getString("reminder_missed", language) + "\n" + message;
                delay = BuildVars.REMINDERTIMESLICEMIN; //send it at the next run()
            }
            BotLogger.info(LOGTAG, "Reminder set in: " + delay);
            reminders.add(new ReminderObject(message, user, delay, time, isTimer));
        } finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * Stops reminders thread (insertion and aging) waiting for it to be terminated
     */
    public void interrupt() {
        lock.lock();
        try {
            isShuttingDown = true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Backup current reminders with their original time (timers are converted to dates) in DB
     * New delay is calculated on restore
     */
    void backupReminders() {
        lock.lock();
        try {
            if (hasReminders)
            {
                try {
                    databaseManager.clearReminders();
                    databaseManager.initializeReminders();
                    for (ReminderObject greap : reminders)
                    {
                        try {
                            databaseManager.insertReminder(greap.getUser(), greap.getBackupTime(), greap.getMessage(), greap.isTimerReminder());
                        } catch (SQLException e) {
                            BotLogger.error(LOGTAG, e);
                        }
                    }
                } catch (SQLException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Restores old reminders from DB.
     * They are all saved as dates and not timers.
     * Reminders are added as new reminders so delay is calculated again from current time.
     */
    private void restoreReminders() {
        ArrayList<Reminder> newReminders = new ArrayList<>();
        lock.lock(); //to be sure that backup and restore are not simultaneous
        try {
            try {
                newReminders = databaseManager.getReminders();
            } catch (SQLException e) {
                if (SQLUtil.genericError(e))
                    BotLogger.info(LOGTAG, "Reminders table does not exist. Nothing to restore."); //Probably
                else
                    BotLogger.error(LOGTAG, e);
            }
            try {
                databaseManager.clearReminders();
            } catch (SQLException ex)   {
                BotLogger.error(LOGTAG, ex);
            }
        } finally {
            lock.unlock();
        }
        if (newReminders.size() > 0)
        {
            for (Reminder reminder : newReminders)
            {
                addReminder(reminder.getUser(), reminder.getMessage(), reminder.getDate(), reminder.isTimerReminder());
            }
        }
    }

    /**
     * Increases reminders age
     * If they reach their target hour they are sent to the user
     * If the user stopped the bot or the bot was removed from the group
     * the method fails with an exception (reminder is not saved)
     */
    public void execute() throws JobExecutionException {
        lock.lock();
        try {
            if (hasReminders && !isShuttingDown)
            {
                for (Iterator<ReminderObject> it = reminders.iterator(); it.hasNext(); )
                {
                    ReminderObject greap = it.next();
                    greap.updateTimeConfig();
                    if (greap.getTimeConfig() == 0)
                    {
                        greap.sendReminder();
                        it.remove(); //so the garbage collector can delete it
                    }
                }
                if (reminders.size() == 0)
                    hasReminders = false;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Class that defines reminders objects
     */
    private class ReminderObject {

        private String reminderMessage;
        private String user;
        private long userCode;
        private long delayTime;
        private String backupTime;
        private int timeConfig = 0; //number of cicle of run to wait before sending the reminder
        private boolean isTimer = false;
        private boolean isGroup = false;

        /**
         * New object, on creation prepares counters and aging
         * @param reminderMessage message to send
         * @param user user or group where to send the message
         * @param delayTime difference in seconds between target hour and current hour
         * @param backupTime time saved as a string for backup and restore purposes
         * @param isTimer if true converts timers to dates
         */
        private ReminderObject(String reminderMessage, String user, long delayTime, String backupTime, boolean isTimer) {
            this.isTimer = isTimer;
            this.user = user;
            this.reminderMessage = reminderMessage;
            this.delayTime = delayTime;
            if (!user.substring(0,1).equals("G"))   // If it isn't a group
            {
                userCode = Long.valueOf(user);
                isGroup = true;
            }
            else
            {
                //removes G and insert '-' at the beginning
                userCode = Math.negateExact(Long.valueOf(user.substring(1,user.length())));
            }
            setTimeConfig();
            if (isTimer)
            {
                int utc;
                if (isGroup)
                    utc = Integer.parseInt(databaseManager.getGroupUTC(userCode));
                else
                    utc = databaseManager.getUserUTC(Long.valueOf(userCode).intValue());
                this.backupTime = Reminder.timerToDate(backupTime, utc);
                this.isTimer = false;
            }
        }

        /**
         * Calculates number of cycles of execute() to perform before sending the reminder
         */
        void setTimeConfig() {
            timeConfig = (int)(long) ( delayTime / BuildVars.REMINDERTIMESLICEMIN );
        }

        /**
         * Called on every execute() decrements the number of cycle before reminder is sent
         */
        void updateTimeConfig() {
            if (timeConfig > 0)
                --timeConfig;
            else
                setTimeConfig();
        }

        /**
         * @return target time as a date
         */
        String getBackupTime() {
            return backupTime;
        }

        /**
         * @return number of cycle of execute() before sending the message
         */
        int getTimeConfig() {
            return timeConfig;
        }

        /**
         * @return user or group who set the reminder
         */
        String getUser() {
            return user;
        }

        /**
         * @return message text
         */
        String getMessage() {
            return reminderMessage;
        }

        /**
         * @return true if reminder is a timer
         */
        boolean isTimerReminder() {
            return isTimer;
        }

        /**
         * Send the reminder to the user or group where it was set
         * If user stopped the bot or bot was removed from group,
         * ignore the reminder and don't backup it in database
         */
        void sendReminder() {
            if (!isGroup && ( databaseManager.isRemovedStatus(Long.valueOf(userCode).intValue()) || !databaseManager.isDBGroup(userCode) ))
                return;
            SendMessage answer = new SendMessage();
            StringBuilder risp = new StringBuilder(BuildVars.REMINDER_DEFAULT_MESS);
            risp.append(reminderMessage);
            answer.setChatId(userCode);
            answer.setText(risp.toString());
            answer.enableHtml(true);
            try {
                CommandsHandler.getInstance().execute(answer);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
            BotLogger.info(LOGTAG, "Reminder sent to: " + userCode);
        }
    }

    /**
     * Compute delay (difference in seconds between target time and current time)
     * @param time time as a timer
     * @return delay
     */
    private long computeTimer(String time) {
        Scanner in = new Scanner(time);
        in.useDelimiter("\\.");
        int days = Integer.parseInt(in.next());
        int hours = Integer.parseInt(in.next());
        int minutes = Integer.parseInt(in.next());
        int seconds = Integer.parseInt(in.next());
        hours += days * 24; //converts days in hours
        long minutesLong = minutes + hours * 60; //converts hours to minutes
        return seconds + minutesLong * 60; //converts minutes to seconds
    }

    /**
     * Compute delay (difference in seconds between target time and current time)
     * @param time time as a date
     * @return delay
     */
    private long computeDate(String time, int utc) {
        Scanner in = new Scanner(time);
        in.useDelimiter("\\.");
        int months = Integer.parseInt(in.next());
        int days = Integer.parseInt(in.next());
        int hours = Integer.parseInt(in.next());
        int minutes = Integer.parseInt(in.next());
        return UtilsMain.computeNextRun(months, days, hours, minutes, utc);
    }
}
