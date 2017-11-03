package krak.miche.KBot.database;

import krak.miche.KBot.services.UtilsMain;
import krak.miche.KBot.structures.LogObject;
import krak.miche.KBot.structures.Reminder;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import static krak.miche.KBot.BuildVars.*;
import static krak.miche.KBot.database.CreationStrings.*;


/**
 * @author Kraktun
 * @version 1.0
 * Database Manager to perform database operations
 */
public class DatabaseManager {
    private static final String LOGTAG = "DATABASEMANAGER";

    private static volatile DatabaseManager instance;
    private static volatile ConnectionDB connection;

    private DatabaseManager() {
        connection = new ConnectionDB();
        try {
            attachGroupsAdminDB();
            attachMiscDB();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        final int currentVersion = connection.checkVersion();
        BotLogger.info(LOGTAG, "Current db version: " + currentVersion);
        if (currentVersion < CreationStrings.version)
        {
            recreateTable(currentVersion);
        }
    }

    public static DatabaseManager getInstance() {
        final DatabaseManager currentInstance;
        if (instance == null)
        {
            synchronized (DatabaseManager.class) {
                if (instance == null)
                {
                    instance = new DatabaseManager();
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

    private void recreateTable(int currentVersion) {
        try {
            connection.initTransaction();
            if (currentVersion == 0)
            {
                currentVersion = updateToVersion1();
            }
            connection.commitTransaction();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    private int updateToVersion1() throws SQLException {
        connection.executeQuery(CREATE_INFO_TABLE);
        connection.executeUPD(INSERT_INFO,  INFO_VERSION, 1, "NULL");
        //connection.executeUPD(UPDATE_INFO, 1, "NULL",INFO_VERSION );
        return 1;
    }

    ResultSet runCustomSQL(String query, String value) throws SQLException{
        return connection.runSqlQuery(query, value);
    }


    //_______________________________________________________________________________________________
    //DATABASE INITIALIZATION

    /**
     * Attaches Misc DB to the main DB
     * @throws SQLException if an error occurs
     */
    private void attachMiscDB()  throws SQLException
    {
        connection.executeQueryAttach(ATTACH_MISC);
    }

    /**
     * Attaches Groups DB to the main DB
     * @throws SQLException if an error occurs
     */
    private void attachGroupsAdminDB()  throws SQLException
    {
        connection.executeQueryAttach(ATTACH_GROUPSADMIN);
    }

    /**
     * Initialize users table
     * @throws SQLException if an error occurs
     */
    public void initializeUsers()  throws SQLException
    {
        if (connection == null)
            instance = new DatabaseManager();
        connection.executeQuery(CREATE_USER_TABLE);
    }

    /**
     * Initialize feedback table
     * @throws SQLException if an error occurs
     */
    public void initializeFeedback()  throws SQLException
    {
        //existence of connection is granted by initializeUsers()
        connection.executeQuery(CREATE_FEEDBACK_TABLE);
    }

    /**
     * Initialize logs table
     * @throws SQLException if an error occurs
     */
    public void initializeLogs()  throws SQLException
    {
        //existence of connection is granted by initializeUsers()
        connection.executeQuery(CREATE_LOG_TABLE);
    }

    /**
     * Initialize reminders table
     * @throws SQLException if an error occurs
     */
    public void initializeReminders()  throws SQLException
    {
        //existence of connection is granted by initializeUsers()
        connection.executeQuery(CREATE_REMINDERS_TABLE);
    }

    /**
     * Initialize command table:
     * This table contains the last command that requires a different handling
     * from the bot. It is used for commands that need parameters in different messages
     * @throws SQLException if an error occurs
     */
    public void initializeCommandTable()  throws SQLException
    {
        //existence of connection is granted by initializeUsers()
        connection.executeQuery(CREATE_COMMANDS_TABLE);
    }

    /**
     * Initialize group table:
     * This table contains a list of all the groups where the bot is active
     * @throws SQLException if an error occurs
     */
    public void initializeGroupTable()  throws SQLException
    {
        //existence of connection is granted by initializeUsers()
        connection.executeQuery(CREATE_GROUP_TABLE);
    }

    /**
     * Initialize user group table:
     * This table named as the group ID contains the users with status for that group
     * Only Selected admins (including group admins) and blacklisted users are saved here, not all users
     * @param groupID ID of the group
     * @throws SQLException if an error occurs
     */
    public void initializeUserGroupTable(Long groupID)  throws SQLException
    {
        //existence of connection is granted by initializeUsers()
        String adapted = replaceInterrogatives(CREATE_USER_GROUP_TABLE, "G" + SQLUtil.longtoString(groupID));
        connection.executeQuery(adapted);
    }

    /**
     * Initialize group settings table:
     * This table named as the group ID contains the settings saved for that group
     * @param groupID ID of the group
     * @throws SQLException if an error occurs
     */
    public void initializeGroupSettingsTable(Long groupID)  throws SQLException
    {
        //existence of connection is granted by initializeUsers()
        String adapted = replaceInterrogatives(CREATE_GROUP_SETTINGS_TABLE, "G" + SQLUtil.longtoString(groupID));
        connection.executeQuery(adapted);
    }


    //_______________________________________________________________________________________________
    //MANAGE USERS TABLE


    /**
     * Check if user is already in DB
     * @return true/false
     */
    public boolean isDBUser(int userDB)
    {
        try {
            ResultSet res = connection.runSqlQuery(LIST_USERS);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userDB == id)
                {
                    res.close();
                    return true;
                }
            }
            res.close();
            return false;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return false;
    }

    /**
     * Retrieve user status from database
     * @return user status
     */
    public String getUserStatus(int userDB)
    {
        try {
            ResultSet res = connection.runSqlQuery(LIST_USERS);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userDB == id)
                {
                    String tp = res.getString("STATUS_USER");
                    res.close();
                    return tp;
                }
            }
            res.close();
            return null;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return null;
    }

    /**
     * Short way to determine if user has used "/stop" command
     * @return true/false
     */
    public boolean isRemovedStatus(int userDB)
    {
        String tempuser = instance.getUserStatus(userDB);
        return tempuser!= null && tempuser.equals(REMOVED_STATUS);
    }

    /**
     * Short way to determine if user is an admin
     * @return true/false
     */
    public boolean isAdminStatus(int userDB)
    {
        String tempuser = instance.getUserStatus(userDB);
        return tempuser.equals(ADMIN_STATUS);
    }

    /**
     * Short way to determine if user is a super admin
     * Use SUPER_ADMIN list instead of this method to determine if an user is a super admin
     * @return true/false
     */
    public boolean isSuperAdminStatus(int userDB)
    {
        String tempuser = instance.getUserStatus(userDB);
        return tempuser.equals(SUPER_ADMIN_STATUS);
    }

    /**
     * Short way to determine if user is blacklisted
     * @return true/false
     */
    public boolean isBlackStatus(int userDB)
    {
        if (!instance.isDBUser(userDB))
            return false;
        String tempuser = instance.getUserStatus(userDB);
        return tempuser.equals(BLACKLISTED_STATUS);
    }

    /**
     * Short way to determine if user is a power user
     * @return true/false
     */
    public boolean isPowerUserStatus(int userDB)
    {
        String tempuser = instance.getUserStatus(userDB);
        return tempuser.equals(POWER_USER_STATUS);
    }

    /**
     * Add user to database with chosen status
     * @param userID = id of the user
     * @param userStatus = status of the user
     * @throws SQLException if an error occurs
     */
    public void addDBUser(int userID, String userStatus, String username, String language, int UTC, String info)  throws SQLException
    {
        connection.executeUPD(ADD_USER, userID, userStatus, username, language, UTC, info);
        UtilsMain.log("Added user " + userID);
    }

    /**
     * Changes user username in users table
     * @param userDB = Id of the user
     * @param newUsername = new status of the user
     * @throws SQLException if an error occurs
     */
    public void changeUserUsername(int userDB, String newUsername) throws SQLException {

        connection.executeUPD(CHANGE_USER_USERNAME, newUsername, userDB);
    }

    /**
     * Changes user UTC Time in users table
     * @param userDB = Id of the user
     * @param UTC = new UTC of the user
     * @throws SQLException if an error occurs
     */
    public void changeUserUTC(int userDB, int UTC) throws SQLException {

        connection.executeUPD(CHANGE_USER_UTC, UTC, userDB);
    }

    /**
     * Changes user status in users table
     * @param userDB = Id of the user
     * @param newStatus = new status of the user
     * @throws SQLException if an error occurs
     */
    public void changeUserStatus(int userDB, String newStatus) throws SQLException {

        newStatus = newStatus.toUpperCase();
        connection.executeUPD(CHANGE_USER_STATUS, newStatus, userDB);
    }

    /**
     * Removes user from users table
     * @param userDB = user to remove
     * @throws SQLException if an error occurs
     */
    public void removeUser(int userDB) throws SQLException {

        connection.executeUPD(REMOVE_USER, userDB);
    }

    /**
     * Retrieve all users in users table with ID and status
     * @return ID + status + username of all users
     * @throws SQLException if an error occurs
     */
    public String getAllUsers() throws SQLException {

        ResultSet res = connection.runSqlQuery(LIST_USERS);
        String toreturn = "";
        while (res.next())
        {
            int id = res.getInt("USER_ID");
            String status = res.getString("STATUS_USER");
            String username = res.getString("USERNAME");
            toreturn = toreturn + id + ": " + status + ": " + username + "\n";
        }
        res.close();
        return toreturn;
    }

    /**
     * Retrieve all admins in users table with ID and status
     * @return username + status of all admins
     * @throws SQLException if an error occurs
     */
    public String getAllAdmins() throws SQLException {

        ResultSet res = connection.runSqlQuery(LIST_USERS);
        String toreturn = "";
        while (res.next())
        {
            int id = res.getInt("USER_ID");
            String status = res.getString("STATUS_USER");
            String username = res.getString("USERNAME");
            if (status.equals(ADMIN_STATUS) || status.equals(SUPER_ADMIN_STATUS))
                toreturn = toreturn + username + ": " + status + "\n";
        }
        res.close();
        return toreturn;
    }

    /**
     * Set user language
     * @param userDB = Id of the user
     * @param language = language
     * @throws SQLException if an error occurs
     */
    public void changeUserLanguage(int userDB, String language) throws SQLException {

        connection.executeUPD(CHANGE_USER_LANGUAGE, language, userDB);
    }

    /**
     * Set user info
     * @param userDB = Id of the user
     * @param info = info
     * @throws SQLException if an error occurs
     */
    public void changeUserInfo(int userDB, String info) throws SQLException {

        connection.executeUPD(CHANGE_USER_INFO, info, userDB);
    }

    /**
     * Retrieve user language from database if not present returns default language
     * @return user language
     * @throws SQLException if an error occurs
     */
    public String getUserLanguage(int userDB)
    {
        try {
            ResultSet res = connection.runSqlQuery(LIST_USERS);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userDB == id)
                {
                    String tp = res.getString("LANGUAGE");
                    res.close();
                    if (tp == null  || tp.equalsIgnoreCase("NULL") || tp.equals(""))
                        tp = DEFAULT_LANG;
                    return tp;
                }
            }
            res.close();
            return DEFAULT_LANG;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return DEFAULT_LANG;
    }

    /**
     * Retrieve user language from database if not present returns default language
     * @return user language
     * @throws SQLException if an error occurs
     */
    public int getUserUTC(int userDB)
    {
        try {
            ResultSet res = connection.runSqlQuery(LIST_USERS);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userDB == id)
                {
                    int tp = res.getInt("UTC_TIME");
                    res.close();
                    return tp;
                }
            }
            res.close();
            return DEFAULT_UTC;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return DEFAULT_UTC;
    }

    //______________________________________________________________________________________
    //MANAGE FEEDBACKS TABLE

    /**
     * Insert feedback into feedback table
     * @param userID = id of the user who sent the feedback
     * @param feedback = feedback to insert
     * @throws SQLException if an error occurs
     */
    public void insertFeedback(int userID, String feedback)  throws SQLException
    {
        if (instance.isDBFeedbackUser(userID))
        {
            feedback = getFeedbackFromUser(userID) + FEEDBACK_DEFAULT_MESS + feedback;
            connection.executeUPD(UPDATE_FEEDBACK, feedback, userID);
        }
        else
            connection.executeUPD(INSERT_FEEDBACK, userID, feedback);
        UtilsMain.log("Received feedback from " + userID);
    }

    /**
     * Returns all the feedback in the feedback table (with Id of who sent it)
     * @return all feedbacks
     * @throws SQLException if an error occurs
     */
    public String getAllFeedbacks() throws SQLException {

        ResultSet res = connection.runSqlQuery(LIST_FEEDBACKS);
        String toreturn = "";
        while (res.next())
        {
            int id = res.getInt("USER_ID");
            String feedback = res.getString("MESSAGE");
            toreturn = toreturn + "#" + id + ": \n" + feedback + "\n";
        }
        res.close();
        return toreturn;
    }

    /**
     * Returns the length of the feedback already stored in the feedback table for selected user
     * @param userID = ID of the user
     * @return feddback length
     * @throws SQLException if an error occurs
     */
    public int getFeedbacklength(int userID) throws SQLException {

        ResultSet res = connection.runSqlQuery(LIST_FEEDBACKS);
        String feedback = "";
        while (res.next())
        {
            int id = res.getInt("USER_ID");
            if (id == userID)
                feedback = res.getString("MESSAGE");
        }
        res.close();
        return feedback.length();
    }

    /**
     * Private method to update a feedback from a user with another feedback
     * @param userID = Id of the user
     * @return old feedback + new feedback
     * @throws SQLException if an error occurs
     */
    private String getFeedbackFromUser(int userID) throws SQLException {

        ResultSet res = connection.runSqlQuery(LIST_FEEDBACKS);
        String toreturn = "";
        while (res.next())
        {
            int id = res.getInt("USER_ID");
            if (id == userID)
            {
                toreturn = res.getString("MESSAGE");
                break;
            }
        }
        res.close();
        return toreturn;
    }

    /**
     * Check if user has already an entry in feedback table
     * @return true/false
     * @throws SQLException if an error occurs
     */
    private boolean isDBFeedbackUser(int userDB)
    {
        try {
            ResultSet res = connection.runSqlQuery(LIST_FEEDBACKS);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userDB == id)
                {
                    res.close();
                    return true;
                }
            }
            res.close();
            return false;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return false;
    }

    /**
     * Deletes feedback table
     * @throws SQLException if an error occurs
     */
    public void clearFeedback() throws SQLException
    {
        connection.executeQuery(CLEAR_FEEDBACK);
    }

    //______________________________________________________________________________________
    //MANAGE LOG TABLE

    /**
     * Insert log into log table
     * @param groupID = Id of the group
     * @param userID = id of the user
     * @param username = username of the user
     * @param date = date of the message
     * @param message = message
     * @throws SQLException if an error occurs
     */
    public void insertLog(long groupID, int userID, String username, String date, String message)  throws SQLException
    {
        String group = "G" + SQLUtil.longtoString(groupID);
        connection.executeUPD(INSERT_GROUP_LOG_TABLE, group, userID, username, date, message);
    }

    /**
     * Returns a list of the messages sent as logObjects
     * @param groupID = ID of the group
     * @return array of logs
     * @throws SQLException if an error occurs
     */
    public ArrayList<LogObject> getLogForGroup(long groupID) throws SQLException {

        String gID = "G" + SQLUtil.longtoString(groupID);
        ResultSet res = connection.runSqlQuery(GET_LOG);
        ArrayList<LogObject> logList = new ArrayList<>();
        while (res.next())
        {
            String id = res.getString("GROUP_ID");
            if (id.equals(gID)) {
                int uID = res.getInt("USER_ID");
                String username = res.getString("USERNAME");
                String date = res.getString("DATE");
                String message = res.getString("MESSAGE");
                logList.add(new LogObject(groupID, uID, username, date, message));
            }
        }
        res.close();
        return logList;
    }

    /**
     * Deletes log table
     * @param groupID ID of the group to clear
     * @throws SQLException if an error occurs
     */
    public void clearLogForGroup(long groupID) throws SQLException
    {
        String gID = "G" + SQLUtil.longtoString(groupID);
        connection.executeUPD(CLEAR_LOG_GROUP, gID);
    }


    //______________________________________________________________________________________
    //MANAGE REMINDERS TABLE

    /**
     * Insert reminder into reminder table
     * @param userID = id of the user who set the reminder
     * @param date = formatted date for the reminder dd.hh.mm.ss
     * @param message = text in reminder
     * @throws SQLException if an error occurs
     */
    public void insertReminder(String userID, String date, String message, boolean isTimer)  throws SQLException
    {
        String timer;
        if (isTimer)
            timer = "TIMER";
        else
            timer = "DATE";
        connection.executeUPD(ADD_REMINDER, userID, date, message, timer);
    }

    /**
     * Returns all the reminders
     * @return all reminders
     * @throws SQLException if an error occurs
     */
    public ArrayList<Reminder> getReminders() throws SQLException {

        ResultSet res = connection.runSqlQuery(LIST_REMINDERS);
        ArrayList<Reminder> toreturn = new ArrayList<>();
        while (res.next())
        {
            String id = res.getString("USER_ID");
            String date = res.getString("DATE");
            String message = res.getString("MESSAGE");
            boolean isTimer = res.getString("TYPE").equals("TIMER");
            toreturn.add(new Reminder(id, date, message, isTimer));
        }
        res.close();
        return toreturn;
    }

    /**
     * Get reminder for selected user
     * @param userID = Id of the user
     * @return Reminder
     * @throws SQLException if an error occurs
     */
    public Reminder getReminderFromUser(int userID) throws SQLException {

        ResultSet res = connection.runSqlQuery(LIST_REMINDERS);
        Reminder toreturn = null;
        while (res.next())
        {
            int id = res.getInt("USER_ID");
            if (id == userID)
            {
                String date = res.getString("DATE");
                String message = res.getString("MESSAGE");
                boolean isTimer = res.getString("TYPE").equals("TIMER");
                toreturn = new Reminder(id + "", date, message, isTimer);
                break;
            }
        }
        res.close();
        return toreturn;
    }

    /**
     * Check if user has already an entry in reminders table
     * @return true/false
     */
    private boolean isDBReminderUser(int userD)
    {
        try {
            ResultSet res = connection.runSqlQuery(LIST_REMINDERS);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userD == id)
                {
                    res.close();
                    return true;
                }
            }
            res.close();
            return false;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);;
        }
        return false;
    }

    /**
     * Deletes reminders table
     * @throws SQLException if an error occurs
     */
    public void clearReminders() throws SQLException
    {
        connection.executeQuery(CLEAR_REMINDERS);
    }

    //______________________________________________________________________________________
    //MANAGE USER_COMMAND TABLE

    /**
     * Check if user has a pending command
     * @param userDB = id of the user to check
     * @return true/false
     */
    public boolean isDBCommandUser(int userDB)
    {
        try {
            ResultSet res = connection.runSqlQuery(LIST_COMMAND_USERS);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userDB == id)
                {
                    res.close();
                    return true;
                }
            }
            res.close();
            return false;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);;
        }
        return false;
    }

    /**
     * Check if user is in command database, if so it adds param1
     * if not it cast an exception.
     * @param userDB = Id of the user
     * @param param1 = param to insert
     * @throws SQLException if an error occurs
     */
    public void insertCommandParam1(int userDB, String param1) throws SQLException {
        String send = INSERT_PARAM1;
        if (instance.isDBCommandUser(userDB))
        {
            connection.executeUPD(send, param1, userDB);
        }
        else
            throw new SQLException("User not in database. Can't add params.");
    }

    /**
     * Check if user is in command database, if so it adds param2
     * if not it cast an exception.
     * @param userDB = Id of the user
     * @param param2 = param to insert
     * @throws SQLException if an error occurs
     */
    public void insertCommandParam2(int userDB, String param2) throws SQLException {
        String send = INSERT_PARAM2;
        if (instance.isDBCommandUser(userDB))
        {
            connection.executeUPD(send, param2, userDB);
        }
        else
            throw new SQLException("User not in database. Can't add params.");
    }

    /**
     * Check if user is in command database, if so it adds param3
     * if not it cast an exception.
     * @param userDB = Id of the user
     * @param param3 = param to insert
     * @throws SQLException if an error occurs
     */
    public void insertCommandParam3(int userDB, String param3) throws SQLException {
        String send = INSERT_PARAM3;
        if (instance.isDBCommandUser(userDB))
        {
            connection.executeUPD(send, param3, userDB);
        }
        else
            throw new SQLException("User not in database. Can't add params.");
    }

    /**
     * Check if user is in command database, if so it updates the command with the new one
     * and resets params if not it adds it.
     * @param userDB = Id of the user
     * @param command = command to insert
     * @throws SQLException if an error occurs
     */
    public void pushCommand(int userDB, String command) throws SQLException {
        String send = INSERT_COMMAND;
        if (instance.isDBCommandUser(userDB))
        {
            send = UPDATE_COMMAND;
            connection.executeUPD(send, command, userDB);
            insertCommandParam1(userDB, "NULL");
            insertCommandParam2(userDB, "NULL");
            insertCommandParam3(userDB, "NULL");
        }
        else
            connection.executeUPD(send, userDB, command);
    }

    /**
     * Returns last command saved for user
     * @param userDB = ID of the user
     * @return last command
     */
    public String getLastCommand(int userDB)
    {
        try {
            ResultSet res = connection.runSqlQuery(LIST_COMMAND_USERS);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userDB == id)
                {
                    String tp = res.getString("COMMAND");
                    res.close();
                    return tp;
                }
            }
            res.close();
            return null;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return null;
    }

    /**
     * Returns last command param1 saved for user
     * @param userDB = ID of the user
     * @return param1
     */
    public String getCommandParam1(int userDB)
    {
        try {
            ResultSet res = connection.runSqlQuery(LIST_COMMAND_USERS);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userDB == id)
                {
                    String tp = res.getString("PARAM1");
                    res.close();
                    return tp;
                }
            }
            res.close();
            return null;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return null;
    }

    /**
     * Returns last command param2 saved for user
     * @param userDB = ID of the user
     * @return param2
     */
    public String getCommandParam2(int userDB)
    {
        try {
            ResultSet res = connection.runSqlQuery(LIST_COMMAND_USERS);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userDB == id)
                {
                    String tp = res.getString("PARAM2");
                    res.close();
                    return tp;
                }
            }
            res.close();
            return null;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return null;
    }

    /**
     * Returns last command param3 saved for user
     * @param userDB = ID of the user
     * @return param3
     */
    public String getCommandParam3(int userDB)
    {
        try {
            ResultSet res = connection.runSqlQuery(LIST_COMMAND_USERS);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userDB == id)
                {
                    String tp = res.getString("PARAM3");
                    res.close();
                    return tp;
                }
            }
            res.close();
            return null;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return null;
    }

    /**
     * Removes user from command table
     * @param userDB = user to remove
     * @throws SQLException if an error occurs
     */
    public void removeUserCommand(int userDB) throws SQLException {

        String send = REMOVE_USER_COMMAND;
        connection.executeUPD(send, userDB);
        UtilsMain.log("Removed user " + userDB + " from command table.");
    }

    //______________________________________________________________________________________
    //MANAGE GROUPS TABLE


    /**
     * Check if group is in database
     * @param chatID group id
     * @return true/false
     */
    public boolean isDBGroup(Long chatID)
    {
        try {
            ResultSet res = connection.runSqlQuery(LIST_GROUPS);
            while (res.next())
            {
                String id = res.getString("GROUP_ID");
                if (( "G" + SQLUtil.longtoString(chatID) ).equals(id))
                {
                    res.close();
                    return true;
                }
            }
            res.close();
            return false;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return false;
    }

    /**
     * Add group to groups database
     * @param groupID = id of he group
     * @throws SQLException if an error occurs
     */
    public void addGroup(Long groupID)  throws SQLException
    {
        connection.executeUPD(ADD_GROUP, "G" + SQLUtil.longtoString(groupID));
        UtilsMain.log("Added group " + groupID);
    }

    /**
     * Retrieve alla groups in groups table
     * @return ID of all groups
     * @throws SQLException if an error occurs
     */
    public String getAllGroups() throws SQLException {

        ResultSet res = connection.runSqlQuery(LIST_GROUPS);
        String toreturn = "";
        while (res.next())
        {
            String id = res.getString("GROUP_ID");
            toreturn = toreturn + id + "\n";
        }
        res.close();
        return toreturn;
    }

    /**
     * Remove group from database (but keeps the settings)
     * @param groupID = id of he group
     * @throws SQLException if an error occurs
     */
    public void removeGroup(Long groupID)  throws SQLException
    {
        connection.executeUPD(REMOVE_GROUP, "G" + SQLUtil.longtoString(groupID));
        UtilsMain.log("Removed group " + groupID);
    }



    //______________________________________________________________________________________
    //MANAGE 'GROUP_ID' TABLE

    /**
     * Add user to group specific database with chosen status
     * @param groupID = id of he group
     * @param userID = id of the user
     * @param userStatus = status of the user
     * @throws SQLException if an error occurs
     */
    public void addGroupUser(Long groupID, int userID, String userStatus, String username, String info)  throws SQLException
    {
        String adapted = replaceInterrogatives(ADD_USER_GROUP, "G" + SQLUtil.longtoString(groupID));
        connection.executeUPD(adapted, userID, userStatus, username, info);
        UtilsMain.log("Added user " + userID + " as " + userStatus + " to group " + groupID);
    }

    /**
     * Changes user status in user group table
     * @param groupID = id of the group
     * @param userDB = Id of the user
     * @param newStatus = new status of the user
     * @throws SQLException if an error occurs
     */
    public void changeUserGroupStatus(Long groupID, int userDB, String newStatus) throws SQLException {

        newStatus = newStatus.toUpperCase();
        String adapted = replaceInterrogatives(CHANGE_USER_GROUP_STATUS, "G" + SQLUtil.longtoString(groupID));
        connection.executeUPD(adapted, newStatus, userDB);
    }

    /**
     * Changes user username in user group table
     * @param groupID = id of the group
     * @param userDB = Id of the user
     * @param newUsername = new username of the user
     * @throws SQLException if an error occurs
     */
    public void changeUserGroupUsername(Long groupID, int userDB, String newUsername) throws SQLException {

        String adapted = replaceInterrogatives(CHANGE_USER_GROUP_USERNAME, "G" + SQLUtil.longtoString(groupID));
        connection.executeUPD(adapted, newUsername, userDB);
    }

    /**
     * Changes user info in user group table
     * @param groupID = id of the group
     * @param userDB = Id of the user
     * @param newInfo = new info for the user
     * @throws SQLException if an error occurs
     */
    public void changeUserGroupInfo(Long groupID, int userDB, String newInfo) throws SQLException {

        String adapted = replaceInterrogatives(CHANGE_USER_GROUP_INFO, "G" + SQLUtil.longtoString(groupID));
        connection.executeUPD(adapted, newInfo, userDB);
    }

    /**
     * Check if user is in selected group
     * @param groupID = id of he group
     * @param userDB = Id of the user
     * @return true/false
     */
    public boolean isUserGroupExist (Long groupID, int userDB) {
        String adapted = replaceInterrogatives(LIST_USERS_GROUP, "G" + SQLUtil.longtoString(groupID));
        try {
            ResultSet res = connection.runSqlQuery(adapted);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userDB == id)
                {
                    res.close();
                    return true;
                }
            }
            res.close();
            return false;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return false;
    }

    /**
     * Check if user in selected group is blacklisted
     * @param groupID = id of he group
     * @param userDB = Id of the user
     * @return true/false
     */
    public boolean isUserGroupBlacklisted (Long groupID, int userDB) {

        if (isUserGroupExist(groupID, userDB))
        {
            String tempuser = instance.getGroupUserStatus(groupID, userDB);
            try {
                return tempuser.equals(BLACKLISTED_STATUS);
            } catch (NullPointerException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Check if user in selected group is an admin
     * @param groupID = id of he group
     * @param userDB = Id of the user
     * @return true/false
     */
    public boolean isUserGroupAdmin (Long groupID, int userDB) {

        if (isUserGroupExist(groupID, userDB))
        {
            String tempuser = instance.getGroupUserStatus(groupID, userDB);
            try {
                return tempuser.equals(ADMIN_STATUS);
            } catch (NullPointerException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Get status for selected user in selected group
     * @param groupID group
     * @param userDB user to check
     * @return status of user
     */
    public String getGroupUserStatus(Long groupID, int userDB)
    {
        String adapted = replaceInterrogatives(LIST_USERS_GROUP, "G" + SQLUtil.longtoString(groupID));
        try {
            ResultSet res = connection.runSqlQuery(adapted);
            while (res.next())
            {
                int id = res.getInt("USER_ID");
                if (userDB == id)
                {
                    String tp = res.getString("STATUS_USER");
                    res.close();
                    return tp;
                }
            }
            res.close();
            return null;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return null;
    }

    /**
     * Retrieve all users in group users table with ID and status (ADMIN\BLACKLISTED)
     * @return ID + status of all users in database
     * @throws SQLException if an error occurs
     */
    public String getAdminsFromGroup(Long groupID) throws SQLException {

        String adapted = replaceInterrogatives(LIST_USERS_GROUP, "G" + SQLUtil.longtoString(groupID));
        ResultSet res = connection.runSqlQuery(adapted);
        String toreturn = "";
        while (res.next())
        {
            int id = res.getInt("USER_ID");
            String status = res.getString("STATUS_USER");
            if (status.equals(ADMIN_STATUS))
                toreturn = toreturn + id + " : " + status + "\n";
        }
        res.close();
        return toreturn;
    }


    //______________________________________________________________________________________
    //MANAGE 'GROUP_ID'_SETTINGS TABLE

    /**
     * Initialize group settings table. Called with /start only on first run
     * @param groupID = id of the group
     * @throws SQLException if an error occurs
     */
    public void initializeGroupSettings(Long groupID) throws SQLException {

        String adapted = replaceInterrogatives(INSERT_GROUP_SETTINGS_TABLE, "G" + SQLUtil.longtoString(groupID));
        connection.executeUPD(adapted, ANTIFLOOD, "false", ANTIFLOOD_DEFAULT_TIME, ANTIFLOOD_DEFAULT_MESS, "NULL" );
        connection.executeUPD(adapted, LANGUAGE, "true", DEFAULT_LANG, "NULL", "NULL");
        connection.executeUPD(adapted, LOG, "false", "default", "NULL", "NULL");
        connection.executeUPD(adapted, UTC, "true", "+2", "NULL", "NULL");

    }

    /**
     * Update antiflood settings (number of message for slice time)
     * Parameters check (if they are int etc) must be done in the calling method
     * @param groupID = id of the group
     * @param activated = true/false if antiflood is activated
     * @param sliceTime =  time to check
     * @param messageNumber = number of messages
     * @throws SQLException if an error occurs
     */
    public void updateGroupSettingsAntiflood(Long groupID, String activated, String sliceTime, String messageNumber) throws SQLException {

        String adapted = replaceInterrogatives(UPDATE_GROUP_SETTINGS_TABLE, "G" + SQLUtil.longtoString(groupID));
        connection.executeUPD(adapted, activated, sliceTime, messageNumber, "NULL", ANTIFLOOD );
    }

    /**
     * Update antiflood settings (number of message for slice time)
     * Parameters check (if they are int etc) must be done in the calling method
     * @param groupID = id of the group
     * @param activated = true/false if antiflood is activated
     * @throws SQLException if an error occurs
     */
    public void updateGroupSettingsAntiflood(Long groupID, String activated) throws SQLException {

        String adapted = replaceInterrogatives(ACTIVATE_GROUP_SETTINGS_TABLE, "G" + SQLUtil.longtoString(groupID));
        connection.executeUPD(adapted, activated, ANTIFLOOD );
    }

    /**
     * Checks if antiflood is active in the group
     * @param groupID = id of the group
     * @return true/false
     */
    public boolean isAntifloodActive(Long groupID)
    {
        String adapted = replaceInterrogatives(GET_GROUP_SETTINGS, "G" + SQLUtil.longtoString(groupID));
        try {
            ResultSet res = connection.runSqlQuery(adapted);
            while (res.next())
            {
                String antiflood = res.getString("SETTING");
                if (antiflood.equals(ANTIFLOOD))
                {
                    String tp = res.getString("ACTIVATED");
                    res.close();
                    return tp.equals("true");
                }
            }
            res.close();
            return false;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return false;
    }

    /**
     * Retrieves antiflood settings (check for consistency is performed when inserted, not extracted)
     * @param groupID = id of the group
     * @return String[2] where 0 = number of messages, 1 = timeSlice
     */
    public String[] getAntifloodSett(Long groupID)
    {
        String adapted = replaceInterrogatives(GET_GROUP_SETTINGS, "G" + SQLUtil.longtoString(groupID));
        String[] ris = new String[2];
        try {
            ResultSet res = connection.runSqlQuery(adapted);
            while (res.next())
            {
                String antiflood = res.getString("SETTING");
                if (antiflood.equals(ANTIFLOOD))
                {
                    ris[1] = res.getString("PARAMONE");
                    ris[0] = res.getString("PARAMTWO");
                    res.close();
                    return ris;
                }
            }
            res.close();
            return null;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return null;
    }

    /**
     * Update language settings
     * Parameters check must be done in the calling method
     * @param groupID = id of the group
     * @param language = language
     * @throws SQLException if an error occurs
     */
    public void updateGroupSettingsLanguage(Long groupID, String language) throws SQLException {

        String adapted = replaceInterrogatives(UPDATE_GROUP_SETTINGS_TABLE, "G" + SQLUtil.longtoString(groupID));
        connection.executeUPD(adapted, "true", language, "NULL", "NULL", LANGUAGE );
    }

    /**
     * Retrieves language (check for consistency is performed when inserted, not extracted)
     * @param groupID = id of the group
     * @return language
     * @throws SQLException if an error occurs
     */
    public String getGroupLanguage(Long groupID)
    {
        String adapted = replaceInterrogatives(GET_GROUP_SETTINGS, "G" + SQLUtil.longtoString(groupID));
        String ris;
        try {
            ResultSet res = connection.runSqlQuery(adapted);
            while (res.next())
            {
                String language = res.getString("SETTING");
                if (language.equals(LANGUAGE))
                {
                    ris = res.getString("PARAMONE");
                    res.close();
                    return ris;
                }
            }
            res.close();
            return DEFAULT_LANG;
        } catch (SQLException e) {
            if (!SQLUtil.genericError(e))
                BotLogger.error(LOGTAG, e);
            //everything ok
        }
        return DEFAULT_LANG;
    }

    /**
     * Update log settings
     * Parameters check must be done in the calling method
     * @param groupID = id of the group
     * @param set = true to enable, false to disable
     * @throws SQLException if an error occurs
     */
    public void setGroupSettingsLog(Long groupID, boolean set) throws SQLException {

        String adapted = replaceInterrogatives(ACTIVATE_GROUP_SETTINGS_TABLE, "G" + SQLUtil.longtoString(groupID));
        if (set)
            connection.executeUPD(adapted, "true", LOG );
        else
            connection.executeUPD(adapted, "false", LOG );
    }

    /**
     * Update log settings
     * Parameters check must be done in the calling method
     * @param groupID = id of the group
     * @param type = default\invert
     * @throws SQLException if an error occurs
     */
    public void updateGroupLogType(Long groupID, String type) throws SQLException {

        String adapted = replaceInterrogatives(UPDATE_GROUP_SETTINGS_TABLE, "G" + SQLUtil.longtoString(groupID));
        connection.executeUPD(adapted, "true", type, "NULL", "NULL", LOG );
    }

    /**
     * Retrieves log mode (check for consistency is performed when inserted, not extracted)
     * @param groupID = id of the group
     * @return log type
     */
    private String getGroupLogType(Long groupID)
    {
        String adapted = replaceInterrogatives(GET_GROUP_SETTINGS, "G" + SQLUtil.longtoString(groupID));
        String ris;
        try {
            ResultSet res = connection.runSqlQuery(adapted);
            while (res.next())
            {
                String log = res.getString("SETTING");
                if (log.equals(LOG))
                {
                    ris = res.getString("PARAMONE");
                    res.close();
                    return ris;
                }
            }
            res.close();
            return null;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return null;
    }

    /**
     * Check if log is default type
     * @param groupID = id of the group
     * @return true/false
     */
    public boolean isGroupLogDefault(Long groupID)
    {
        return getGroupLogType(groupID)!=null && getGroupLogType(groupID).equals("default");
    }

    /**
     * Checks if logging is active in the group
     * @param groupID = id of the group
     * @return true/false
     */
    public boolean isLogActive(Long groupID)
    {
        String adapted = replaceInterrogatives(GET_GROUP_SETTINGS, "G" + SQLUtil.longtoString(groupID));
        try {
            ResultSet res = connection.runSqlQuery(adapted);
            while (res.next())
            {
                String antiflood = res.getString("SETTING");
                if (antiflood.equals(LOG))
                {
                    String tp = res.getString("ACTIVATED");
                    res.close();
                    return tp.equals("true");
                }
            }
            res.close();
            return false;
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return false;
    }

    /**
     * Update utc settings
     * Parameters check must be done in the calling method
     * @param groupID = id of the group
     * @param UTCvalue = default\invert
     * @throws SQLException if an error occurs
     */
    public void updateGroupUTC(Long groupID, String UTCvalue) throws SQLException {

        String adapted = replaceInterrogatives(UPDATE_GROUP_SETTINGS_TABLE, "G" + SQLUtil.longtoString(groupID));
        connection.executeUPD(adapted, "true", UTCvalue, "NULL", "NULL", UTC );
    }

    /**
     * Retrieves utc (check for consistency is performed when inserted, not extracted), default if not present
     * @param groupID = id of the group
     * @return utc
     */
    public String getGroupUTC(Long groupID)
    {
        String adapted = replaceInterrogatives(GET_GROUP_SETTINGS, "G" + SQLUtil.longtoString(groupID));
        String ris;
        try {
            ResultSet res = connection.runSqlQuery(adapted);
            while (res.next())
            {
                String log = res.getString("SETTING");
                if (log.equals(UTC))
                {
                    ris = res.getString("PARAMONE");
                    res.close();
                    return ris;
                }
            }
            res.close();
            return DEFAULT_UTC + "";
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        return DEFAULT_UTC + "";
    }

    //______________________________________________________________________________________
    //UTILS

    /**
     * Executes a custom sql command
     * @param query query to execute
     * @throws SQLException if an error occurs
     */
    public void executeCustomSQL(String query) throws SQLException {

        connection.executeQuery(query);
    }


    /**
     * Replaces the first ? with the param and returns the resulting query
     *
     * @param query query with ? to replace
     * @param param1 param to insert
     * @return query with the first ? replaced with param1
     */
    private String replaceInterrogatives(String query, String param1)
    {
        Scanner in = new Scanner (query);
        in.useDelimiter("\\?");
        String firstHalf = in.next();
        String secondHalf = in.nextLine();
        in.close();
        secondHalf = secondHalf.substring(1, secondHalf.length());
        return firstHalf +  param1 + secondHalf;
    }


    /**
     * Closes connection with database
     * @throws SQLException if an error occurs
     */
    public void shutdown()  throws SQLException
    {
        connection.closeConnection();
    }

}
