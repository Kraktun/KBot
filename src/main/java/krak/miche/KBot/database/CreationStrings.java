package krak.miche.KBot.database;


/**
 * @author Kraktun
 * @version 1.0
 * Class to store strings to execute in database
 * See below for a description of the structure of the DB
 */

class CreationStrings {
    static final int version = 1;

    static final String CREATE_INFO_TABLE = "CREATE TABLE IF NOT EXISTS UTILS (INFO text PRIMARY KEY, NUM integer, STRING text)";
    static final String INSERT_INFO = "INSERT INTO UTILS (INFO, NUM, STRING) VALUES (?, ?, ?)";
    static final String UPDATE_INFO = "UPDATE UTILS SET NUM = ?, STRING = ? WHERE INFO = ?";
    static final String INFO_VERSION = "VERSION";
    static final String LIST_INFO = "SELECT INFO, NUM, STRING FROM UTILS";

    static final String ATTACH_MISC = "ATTACH DATABASE '/media/sdh1/home/thek/private/file-base-p/TBot/SQLiteDB/MiscDB.db' AS 'MISC'";
    static final String ATTACH_GROUPSADMIN = "ATTACH DATABASE '/media/sdh1/home/thek/private/file-base-p/TBot/SQLiteDB/GroupsAdmin.db' AS 'GROUPSADMIN'";

    static final String CREATE_REMINDERS_TABLE = "CREATE TABLE IF NOT EXISTS REMINDERS (USER_ID text PRIMARY KEY, DATE text, MESSAGE text, TYPE text)";
    static final String ADD_REMINDER = "INSERT INTO REMINDERS (USER_ID, DATE, MESSAGE, TYPE) VALUES (?, ?, ?, ?)";
    static final String LIST_REMINDERS = "SELECT USER_ID, DATE, MESSAGE, TYPE FROM REMINDERS";
    static final String CLEAR_REMINDERS = "DROP TABLE IF EXISTS REMINDERS";

    static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS USERS (USER_ID integer PRIMARY KEY, STATUS_USER text, USERNAME text, LANGUAGE text, UTC_TIME integer, INFO text)";
    static final String CHANGE_USER_STATUS = "UPDATE USERS SET STATUS_USER = ? WHERE USER_ID = ?";
    static final String CHANGE_USER_USERNAME = "UPDATE USERS SET USERNAME = ? WHERE USER_ID = ?";
    static final String CHANGE_USER_UTC = "UPDATE USERS SET UTC_TIME = ? WHERE USER_ID = ?";
    static final String ADD_USER = "INSERT INTO USERS (USER_ID, STATUS_USER, USERNAME, LANGUAGE, UTC_TIME, INFO) VALUES (?, ?, ?, ?, ?, ?)";
    static final String REMOVE_USER = "DELETE FROM USERS WHERE USER_ID in (?)";
    static final String LIST_USERS = "SELECT USER_ID, STATUS_USER, USERNAME, LANGUAGE, UTC_TIME, INFO FROM USERS";
    static final String CHANGE_USER_LANGUAGE = "UPDATE USERS SET LANGUAGE = ? WHERE USER_ID = ?";
    static final String CHANGE_USER_INFO = "UPDATE USERS SET INFO = ? WHERE USER_ID = ?";

    static final String CREATE_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS GROUPS (GROUP_ID text PRIMARY KEY)";
    static final String ADD_GROUP = "INSERT INTO GROUPS (GROUP_ID) VALUES (?)";
    static final String REMOVE_GROUP = "DELETE FROM GROUPS WHERE GROUP_ID in (?)";
    static final String LIST_GROUPS = "SELECT GROUP_ID FROM GROUPS";

    static final String CREATE_FEEDBACK_TABLE = "CREATE TABLE IF NOT EXISTS MISC.FEEDBACKS (USER_ID integer PRIMARY KEY, MESSAGE text)";
    static final String INSERT_FEEDBACK = "INSERT INTO MISC.FEEDBACKS (USER_ID, MESSAGE) VALUES (?, ?)";
    static final String LIST_FEEDBACKS = "SELECT USER_ID, MESSAGE FROM MISC.FEEDBACKS";
    static final String UPDATE_FEEDBACK = "UPDATE MISC.FEEDBACKS SET MESSAGE = ? WHERE USER_ID =?";
    static final String CLEAR_FEEDBACK = "DROP TABLE IF EXISTS MISC.FEEDBACKS";

    static final String CREATE_COMMANDS_TABLE = "CREATE TABLE IF NOT EXISTS MISC.USER_COMMAND (USER_ID integer PRIMARY KEY, COMMAND text, PARAM1 text, PARAM2 text, PARAM3 text)";
    static final String UPDATE_COMMAND = "UPDATE MISC.USER_COMMAND SET COMMAND = ? WHERE USER_ID = ?";
    static final String INSERT_COMMAND = "INSERT INTO MISC.USER_COMMAND (USER_ID, COMMAND, PARAM1, PARAM2, PARAM3) VALUES (?, ?, NULL, NULL, NULL)";
    static final String INSERT_PARAM1 = "UPDATE MISC.USER_COMMAND SET PARAM1 = ? WHERE USER_ID = ?";
    static final String INSERT_PARAM2 = "UPDATE MISC.USER_COMMAND SET PARAM2 = ? WHERE USER_ID = ?";
    static final String INSERT_PARAM3 = "UPDATE MISC.USER_COMMAND SET PARAM3 = ? WHERE USER_ID = ?";
    static final String LIST_COMMAND_USERS = "SELECT USER_ID, COMMAND, PARAM1, PARAM2, PARAM3 FROM MISC.USER_COMMAND";
    static final String REMOVE_USER_COMMAND = "DELETE FROM MISC.USER_COMMAND WHERE USER_ID in (?)";

    static final String CREATE_LOG_TABLE = "CREATE TABLE IF NOT EXISTS MISC.LOG (NUM integer PRIMARY KEY, GROUP_ID text, USER_ID INT, USERNAME text, DATE text, MESSAGE text)";
    static final String INSERT_GROUP_LOG_TABLE = "INSERT INTO MISC.LOG (GROUP_ID, USER_ID, USERNAME, DATE, MESSAGE) VALUES (?,?,?,?,?)";
    static final String GET_LOG = "SELECT NUM, GROUP_ID, USER_ID, USERNAME, DATE, MESSAGE FROM MISC.LOG";
    static final String CLEAR_LOG_GROUP = "DELETE FROM MISC.LOG WHERE GROUP_ID in (?)";

    static final String CREATE_USER_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS GROUPSADMIN.? (USER_ID integer PRIMARY KEY, STATUS_USER text, USERNAME text, INFO text)";
    static final String CHANGE_USER_GROUP_STATUS = "UPDATE GROUPSADMIN.? SET STATUS_USER = ? WHERE USER_ID = ?";
    static final String CHANGE_USER_GROUP_USERNAME = "UPDATE GROUPSADMIN.? SET USERNAME = ? WHERE USER_ID = ?";
    static final String CHANGE_USER_GROUP_INFO = "UPDATE GROUPSADMIN.? SET INFO = ? WHERE USER_ID = ?";
    static final String ADD_USER_GROUP = "INSERT INTO GROUPSADMIN.? (USER_ID, STATUS_USER, USERNAME, INFO) VALUES (?, ?, ?, ?)";
    static final String REMOVE_USER_GROUP = "DELETE FROM GROUPSADMIN.? WHERE USER_ID in (?)";
    static final String LIST_USERS_GROUP = "SELECT USER_ID, STATUS_USER, USERNAME, INFO FROM GROUPSADMIN.?";

    static final String CREATE_GROUP_SETTINGS_TABLE = "CREATE TABLE IF NOT EXISTS GROUPSADMIN.?SETTINGS (SETTING text PRIMARY KEY, ACTIVATED text, PARAMONE text, PARAMTWO text, PARAMTHREE text)";
    static final String UPDATE_GROUP_SETTINGS_TABLE = "UPDATE GROUPSADMIN.?SETTINGS SET ACTIVATED = ?, PARAMONE = ?, PARAMTWO = ?, PARAMTHREE = ? WHERE SETTING = ?";
    static final String INSERT_GROUP_SETTINGS_TABLE = "INSERT INTO GROUPSADMIN.?SETTINGS (SETTING, ACTIVATED, PARAMONE, PARAMTWO, PARAMTHREE) VALUES (?,?,?,?,?)";
    static final String ANTIFLOOD = "ANTIFLOOD";
    static final String LANGUAGE = "LANGUAGE";
    static final String LOG = "LOG";
    static final String UTC = "UTC";
    static final String ACTIVATE_GROUP_SETTINGS_TABLE = "UPDATE GROUPSADMIN.?SETTINGS SET ACTIVATED = ? WHERE SETTING = ?";
    static final String GET_GROUP_SETTINGS = "SELECT SETTING, ACTIVATED, PARAMONE, PARAMTWO, PARAMTHREE FROM GROUPSADMIN.?SETTINGS";

    static final String GENERIC_SEARCH = "SELECT * FROM # WHERE # = ?";



    /*
       Names of the tables:
       DB:
       - Table: UTILS contains useful info about the database (for now only version).
            INFO = name of the info
            NUM = integer associated to the info
            STRING = string associated to the info
       - Table: USERS contains USER_ID, STATUS_USER (admin, user etc), USERNAME (@username), LANGUAGE (code), UTC_TIME, INFO (name, reason for ban etc)
            Status: USER, ADMIN, BLACKLISTED, SUPER_ADMIN, POWER_USER, REMOVED
       - Table : GROUPS contains GROUP_ID
            it's a list of all the groups where someone added the bot and typed /start
            in the form 'G19989827'
       - Table : REMINDERS contains USER_ID (id of the user who set the reminder), DATE (formatted date for the reminder),
                MESSAGE (actual reminder). It is used only as a backup method when the bot is stopped/restarted.
       - Table: FEEDBACKS contains USER_ID and MESSAGE
       - Table: USER_COMMAND contains USER_ID and COMMAND = last command sent by the user (used for ask-answer commands)
        Contains also up to three params
       - Table : LOG contains GROUP_ID, USER_ID, USERNAME, DATE, MESSAGE
        this table is used to log messages in a group

     * GROUPSADMIN
       //In the following tables 'G' stands for Group, in the future I can distinguish between channels, super groups etc...
       - Table : 'GROUP_ID' contains USER_ID, STATUS_USER (admin, user, blacklisted...), USERNAME, INFO
            for the group, where GROUP_ID is the number of the group. Ex: 'GROUP_ID' = 'G77775678765'
            USERNAME is the username of the user and INFO is used to store info for users (name, reason for ban etc)
       - Table : 'GROUP_ID'SETTINGS contains the settings for the group, where SETTING is the name of the setting, ACTIVATED is true/false
            if the setting is activated, where PARAMONE/PARAMTWO/PARAMTHREE are the additional params for the selected setting
        ANTIFLOOD: ACTIVATED = true\false, PARAMONE = sliceTime, PARAMTWO = messageNumber, PARAMTHREE = "NULL"
        LANGUAGE: ACTIVATED = true (default), PARAMONE = language code
        LOG: ACTIVATED = true\false, PARAMONE = mode (default\invert)
        UTC: ACTIVATED = true (default), PARAMONE = utc (es. +2)

     */
}
