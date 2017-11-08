package krak.miche.KBot.structures;


import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * @author Kraktun
 * @version 1.0
 * Utility class to store temporary reminders
 * in the process database->handler
 */
public class Reminder {

    private String user;
    private String date;
    private String message;
    private boolean isTimer;

    public Reminder(String user, String date, String message, boolean isTimer) {
        this.user = user;
        this.date = date;
        this.message = message;
        this.isTimer = isTimer;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public boolean isTimerReminder() {
        return isTimer;
    }

    /**
     * Check if text sent as reminder is a timer or a date
     * @param type type to check between timer and date
     * @param text formatted timer/date to check
     * @return true if check is successful
     */
    public static boolean isFormattedReminder(String type, String text) {
        if (type == null || text == null)
            return false;
        if (type.equalsIgnoreCase("t"))
            return isFormattedTimer(text);
        else if (type.equalsIgnoreCase("d"))
            return isFormattedDate(text);
        return false;
    }

    /**
     * Check if string is a formatted timer
     * @param text string to check
     * @return true if is a correct timer
     */
    private static boolean isFormattedTimer(String text) {
        try {
            Scanner in = new Scanner(text);
            in.useDelimiter("\\.");
            int days = Integer.parseInt(in.next());
            int hours = Integer.parseInt(in.next());
            int minutes = Integer.parseInt(in.next());
            int seconds = Integer.parseInt(in.next());
            in.close();
            return !( days > 99 || hours > 23 || minutes > 59 || seconds > 59 || days < 0 || hours < 0 || minutes < 0
             || seconds < 0 || (days == 0 && hours == 0 && minutes == 0 && seconds == 0));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if string is a formatted date
     * @param text string to check
     * @return true if is a correct date
     */
    private static boolean isFormattedDate(String text) {
        try {
            Scanner in = new Scanner(text);
            in.useDelimiter("\\.");
            int months = Integer.parseInt(in.next());
            int days = Integer.parseInt(in.next());
            int hours = Integer.parseInt(in.next());
            int minutes = Integer.parseInt(in.next());
            in.close();
            LocalDateTime localNow = LocalDateTime.now(Clock.systemUTC());
            int year = localNow.getYear();
            LocalDateTime.of(year, months, days, hours, minutes);
            //if it does not throw an exception it's a valid input.
            return !( months > 12 || days > 99 || hours > 23 || minutes > 59 || months < 0 || days < 0 || hours < 0 || minutes < 0
                    || (days == 0 && hours == 0 && minutes == 0 && months == 0));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Converts timers to dates
     * @param timer string containing the timer
     * @param UTC target utc
     * @return timer converted to a date format
     */
    public static String timerToDate(String timer, int UTC) {
        Scanner in = new Scanner(timer);
        in.useDelimiter("\\.");
        int days = Integer.parseInt(in.next());
        int hours = Integer.parseInt(in.next());
        int minutes = Integer.parseInt(in.next());
        int seconds = Integer.parseInt(in.next());
        in.close();
        LocalDateTime localNow = LocalDateTime.now(Clock.systemUTC());
        localNow = localNow.plusHours(UTC);
        localNow = localNow.plusDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        return localNow.getMonthValue() + "." + localNow.getDayOfMonth() + "." + localNow.getHour() + "." + localNow.getMinute();
    }
}
