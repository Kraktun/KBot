package krak.miche.KBot;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kraktun
 * @version 1.0
 */

public class BuildVars {
    public static final List<Integer> SUPER_ADMINS = new ArrayList<>();
    public static final List<String> SUPER_ADMINS_USERNAMES = new ArrayList<>();
    public static final List<Integer> ADMINS = new ArrayList<>();
    public static final List<String> ADMINS_USERNAMES = new ArrayList<>();
    public static final List<Integer> POWER_USER = new ArrayList<>();
    public static final List<String> POWER_USER_USERNAMES = new ArrayList<>();
    public static final List<Integer> BLACK_LIST = new ArrayList<>();
    public static final List<String> BLACK_LIST_USERNAMES = new ArrayList<>();
    public static final int THREADS_INIT_DELAY = 15;
    public static final int ANTIFLOODTIMESLICEMIN = 2;
    public static final Long TASKSHUTDOWN = 2000L; //Milliseconds
    public static final String ANTIFLOOD_DEFAULT_MESS = 10 + "";
    public static final String ANTIFLOOD_DEFAULT_TIME = 5 +"";
    public static final int REMINDERTIMESLICEMIN = 2;
    public static final String REMINDER_DEFAULT_MESS = "<b>NEW REMINDER: \n</b>";
    public static final String FEEDBACK_DEFAULT_MESS = "\nNEW MESSAGE:\n";
    public static final String LOG_SPECIAL_CHAR = "$";
    public static boolean ignoreOldUpdates = true; //ignore updates received while the bot was offline
    public static String DEFAULT_LANG = "en";
    public static final int DEFAULT_UTC = +1;
    public static String DEFAULT_INFO = "";
    public static boolean isTestBotEnabled = true; //True if you want to use test bot
    public static boolean isMainBotEnabled = false; //True if you want to use main bot

    public static final int MAX_REMINDER_LENGTH = 2500;
    public static final int MAX_FEEDBACK_LENGTH = 2500;
    public static final int MAX_ADDED_FEEDBACK = FEEDBACK_DEFAULT_MESS.replaceAll("\\n","nn").length(); //Characters added when you create a feedback (along the message)
    //not sure if in the database "\n" are considered 1 or 2 char
    public static final String BOT_NAME = ""; //Bot name
    public static final String BOT_USERNAME = ""; //Bot username without @
    public static final String BOT_TEST_USERNAME = ""; //Bot username
    public static int BOT_ID= 0;// Bot ID
    public static final int BOT_TEST_ID = 0;
    //Status for users in database
    public static final String ADMIN_STATUS = "ADMIN";
    public static final String POWER_USER_STATUS = "POWER_USER";
    public static final String REMOVED_STATUS = "REMOVED";
    public static final String SUPER_ADMIN_STATUS = "SUPER_ADMIN";
    public static final String BLACKLISTED_STATUS = "BLACKLISTED";
    public static final String USER_STATUS = "USER";
    public static final String UNKNOWN_USERNAME = "UNKNOWN";

    //DATABASE INFO
    //public static final String linkDB = "jdbc:derby:/path/to/your/database;create=true";
    //public static final String controllerDB = "org.apache.derby.jdbc.EmbeddedDriver";
    //public static final String usernameDB = "";
    //public static final String password = "";
    public static final String linkDB = "jdbc:sqlite:/path/to/your/database.db";
    public static final String controllerDB = "org.sqlite.JDBC";

    static {
        // Add elements to arrays here
        SUPER_ADMINS.add(BOT_ID); //BOT ID
        SUPER_ADMINS_USERNAMES.add("@" + BOT_USERNAME);
        SUPER_ADMINS.add(BOT_TEST_ID); //BOT ID
        SUPER_ADMINS_USERNAMES.add("@" + BOT_TEST_USERNAME);
    }
}
