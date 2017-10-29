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

    public static boolean isFormattedReminder(String type, String text) {
        if (type.equalsIgnoreCase("t"))
            return isFormattedTimer(text);
        else if (type.equalsIgnoreCase("d"))
            return isFormattedDate(text);
        return false;
    }

    private static boolean isFormattedTimer(String text) {
        try {
            Scanner in = new Scanner(text);
            in.useDelimiter("\\.");
            int days = Integer.parseInt(in.next());
            int hours = Integer.parseInt(in.next());
            int minutes = Integer.parseInt(in.next());
            int seconds = Integer.parseInt(in.next());
            in.close();
            return !( days > 99 || hours > 23 || minutes > 59 || seconds > 59 );
        } catch (Exception e) {
            return false;
        }
    }

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
            return true;
        } catch (Exception e) {
            return false;
        }
    }

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
