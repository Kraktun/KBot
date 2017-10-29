package krak.miche.KBot.database;

import static krak.miche.KBot.BuildVars.MAX_FEEDBACK_LENGTH;
import static krak.miche.KBot.BuildVars.MAX_REMINDER_LENGTH;

/**
 * @author Kraktun
 * @version 1.0
 * Class to store strings to execute in database
 */

class CreationStringsDerby {
    static final int version = 9;

    static final String CREATE_INFO_TABLE = "CREATE TABLE ADMIN.UTILS (INFO VARCHAR(20) PRIMARY KEY, NUM INT, STRING VARCHAR(50))";
    static final String INSERT_INFO = "INSERT INTO ADMIN.UTILS VALUES (?, ?, ?)";
    static final String UPDATE_INFO = "UPDATE ADMIN.UTILS SET NUM = ?, STRING = ? WHERE INFO = ?";
    static final String INFO_VERSION = "VERSION";
    static final String LIST_INFO = "SELECT * FROM ADMIN.UTILS";

    static final String CREATE_MAIN_SCHEMA = "CREATE SCHEMA ADMIN";
    static final String CREATE_MISC_SCHEMA = "CREATE SCHEMA MISC";
    static final String CREATE_GROUPS_SCHEMA = "CREATE SCHEMA GROUPSADMIN";

    static final String CREATE_REMINDERS_TABLE = "CREATE TABLE ADMIN.REMINDERS (USER_ID VARCHAR(50) PRIMARY KEY, DATE VARCHAR(50), MESSAGE VARCHAR(" + MAX_REMINDER_LENGTH + "), TYPE VARCHAR(10))";
    static final String ADD_REMINDER = "INSERT INTO ADMIN.REMINDERS VALUES (?, ?, ?, ?)";
    static final String LIST_REMINDERS = "SELECT * FROM ADMIN.REMINDERS";
    static final String CLEAR_REMINDERS = "DROP TABLE ADMIN.REMINDERS";

    static final String CREATE_USER_TABLE = "CREATE TABLE ADMIN.USERS (USER_ID INT PRIMARY KEY, STATUS_USER VARCHAR(20), USERNAME VARCHAR(200), LANGUAGE VARCHAR(10), UTC_TIME INT, INFO VARCHAR(500))";
    static final String CHANGE_USER_STATUS = "UPDATE ADMIN.USERS SET STATUS_USER = ? WHERE USER_ID = ?";
    static final String CHANGE_USER_USERNAME = "UPDATE ADMIN.USERS SET USERNAME = ? WHERE USER_ID = ?";
    static final String CHANGE_USER_UTC = "UPDATE ADMIN.USERS SET UTC_TIME = ? WHERE USER_ID = ?";
    static final String ADD_USER = "INSERT INTO ADMIN.USERS VALUES (?, ?, ?, ?, ?, ?)";
    static final String REMOVE_USER = "DELETE FROM ADMIN.USERS WHERE USER_ID in (?)";
    static final String LIST_USERS = "SELECT * FROM ADMIN.USERS";
    static final String CHANGE_USER_LANGUAGE = "UPDATE ADMIN.USERS SET LANGUAGE = ? WHERE USER_ID = ?";
    static final String CHANGE_USER_INFO = "UPDATE ADMIN.USERS SET INFO = ? WHERE USER_ID = ?";
    static final String UPDATE_TO_V3 = "ALTER TABLE ADMIN.USERS ADD COLUMN USERNAME VARCHAR(200)";
    static final String UPDATE_TO_V4 = "ALTER TABLE ADMIN.USERS ADD COLUMN LANGUAGE VARCHAR(10)";
    static final String UPDATE_TO_V6 = "ALTER TABLE ADMIN.USERS ADD COLUMN UTC_TIME INT";
    static final String UPDATE_TO_V7_1 = "ALTER TABLE ADMIN.USERS ADD COLUMN INFO VARCHAR(500)";

    static final String CREATE_FEEDBACK_TABLE = "CREATE TABLE MISC.FEEDBACKS (USER_ID INT PRIMARY KEY, MESSAGE VARCHAR(" + MAX_FEEDBACK_LENGTH + "))";
    static final String INSERT_FEEDBACK = "INSERT INTO MISC.FEEDBACKS VALUES (?, ?)";
    static final String LIST_FEEDBACKS = "SELECT * FROM MISC.FEEDBACKS";
    static final String UPDATE_FEEDBACK = "UPDATE MISC.FEEDBACKS SET MESSAGE = ? WHERE USER_ID =?";
    static final String CLEAR_FEEDBACK = "DROP TABLE MISC.FEEDBACKS";

    static final String CREATE_COMMANDS_TABLE = "CREATE TABLE MISC.USER_COMMAND (USER_ID INT PRIMARY KEY, COMMAND VARCHAR(20), PARAM1 VARCHAR(20), PARAM2 VARCHAR(20), PARAM3 VARCHAR(20))";
    static final String UPDATE_COMMAND = "UPDATE MISC.USER_COMMAND SET COMMAND = ? WHERE USER_ID = ?";
    static final String INSERT_COMMAND = "INSERT INTO MISC.USER_COMMAND VALUES (?, ?, NULL, NULL, NULL)";
    static final String INSERT_PARAM1 = "UPDATE MISC.USER_COMMAND SET PARAM1 = ? WHERE USER_ID = ?";
    static final String INSERT_PARAM2 = "UPDATE MISC.USER_COMMAND SET PARAM2 = ? WHERE USER_ID = ?";
    static final String INSERT_PARAM3 = "UPDATE MISC.USER_COMMAND SET PARAM3 = ? WHERE USER_ID = ?";
    static final String LIST_COMMAND_USERS = "SELECT * FROM MISC.USER_COMMAND";
    static final String REMOVE_USER_COMMAND = "DELETE FROM MISC.USER_COMMAND WHERE USER_ID in (?)";

    static final String CREATE_LOG_TABLE = "CREATE TABLE MISC.LOG (NUM INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, GROUP_ID VARCHAR(50), USER_ID INT, USERNAME VARCHAR(200), DATE VARCHAR(50), MESSAGE VARCHAR(1000))";
    static final String INSERT_GROUP_LOG_TABLE = "INSERT INTO MISC.LOG (GROUP_ID, USER_ID, USERNAME, DATE, MESSAGE) VALUES (?,?,?,?,?)";
    static final String GET_LOG = "SELECT * FROM MISC.LOG";
    static final String CLEAR_LOG_GROUP = "DELETE FROM MISC.LOG WHERE GROUP_ID in (?)";

    static final String CREATE_GROUP_TABLE = "CREATE TABLE ADMIN.GROUPS (GROUP_ID VARCHAR(50) PRIMARY KEY)";
    static final String ADD_GROUP = "INSERT INTO ADMIN.GROUPS VALUES (?)";
    static final String REMOVE_GROUP = "DELETE FROM ADMIN.GROUPS WHERE GROUP_ID in (?)";
    static final String LIST_GROUPS = "SELECT * FROM ADMIN.GROUPS";

    static final String CREATE_USER_GROUP_TABLE = "CREATE TABLE GROUPSADMIN.? (USER_ID INT PRIMARY KEY, STATUS_USER VARCHAR(20), USERNAME VARCHAR(200), INFO VARCHAR(500))";
    static final String CHANGE_USER_GROUP_STATUS = "UPDATE GROUPSADMIN.? SET STATUS_USER = ? WHERE USER_ID = ?";
    static final String CHANGE_USER_GROUP_USERNAME = "UPDATE GROUPSADMIN.? SET USERNAME = ? WHERE USER_ID = ?";
    static final String CHANGE_USER_GROUP_INFO = "UPDATE GROUPSADMIN.? SET INFO = ? WHERE USER_ID = ?";
    static final String ADD_USER_GROUP = "INSERT INTO GROUPSADMIN.? VALUES (?, ?, ?, ?)";
    static final String REMOVE_USER_GROUP = "DELETE FROM GROUPSADMIN.? WHERE USER_ID in (?)";
    static final String LIST_USERS_GROUP = "SELECT * FROM GROUPSADMIN.?";
    static final String UPDATE_TO_V7_2 = "ALTER TABLE GROUPSADMIN.? ADD COLUMN USERNAME VARCHAR(200)";
    static final String UPDATE_TO_V7_3 = "ALTER TABLE GROUPSADMIN.? ADD COLUMN INFO VARCHAR(500)";

    static final String CREATE_GROUP_SETTINGS_TABLE = "CREATE TABLE GROUPSADMIN.?SETTINGS (SETTING VARCHAR(20) PRIMARY KEY, ACTIVATED VARCHAR(10), PARAMONE VARCHAR(20), PARAMTWO VARCHAR(20), PARAMTHREE VARCHAR(20))";
    static final String UPDATE_GROUP_SETTINGS_TABLE = "UPDATE GROUPSADMIN.?SETTINGS SET ACTIVATED = ?, PARAMONE = ?, PARAMTWO = ?, PARAMTHREE = ? WHERE SETTING = ?";
    static final String INSERT_GROUP_SETTINGS_TABLE = "INSERT INTO GROUPSADMIN.?SETTINGS VALUES (?,?,?,?,?)";
    static final String ANTIFLOOD = "ANTIFLOOD";
    static final String LANGUAGE = "LANGUAGE";
    static final String LOG = "LOG";
    static final String UTC = "UTC";
    static final String ACTIVATE_GROUP_SETTINGS_TABLE = "UPDATE GROUPSADMIN.?SETTINGS SET ACTIVATED = ? WHERE SETTING = ?";
    static final String GET_GROUP_SETTINGS = "SELECT * FROM GROUPSADMIN.?SETTINGS";




    /*
       Names of the tables:
       Schema:
     * ADMIN
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
     * MISC
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
