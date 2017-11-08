package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;
import krak.miche.KBot.services.Localizer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import static krak.miche.KBot.BuildVars.*;
import static krak.miche.KBot.database.SQLUtil.*;

//Better to use queues next time
/**
 * @author Kraktun
 * @version 1.0
 * Every BuildVars.ANTIFLOODTIMESLICEMIN the AntifloodJob runs the execute(), this increases messages age by 1
 * If age is greater than the Time slice in the selected group the message is removed from the list
 * Every time a message is sent in a group, it is inserted in its sender's list (for a specific group)
 * if with that message the user has sent more than the allowed Number of Messages it is signaled to the calling method
 * @todo Remove unused user/group objects so the garbage collector can delete them
 */

public class AntiFloodHandler {

    /*
     * Note that time slices don't start when someone set the antiflood, so if you chose 6 sec
     * and in the first 2 seconds someone exceeds the max number of messages, but the bot ends its
     * timeslice(ANTIFLOODTIMESLICEMIN) after the first second, the user is not removed
     */
    public static final String LOGTAG = "ANTIFLOODHANDLER";
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private ArrayList<GroupObject> groups;
    private final ReentrantLock lock = new ReentrantLock();
    private static volatile AntiFloodHandler instance;
    private static boolean isShuttingDown = false;


    /**
     * Private constructor
     * Initialize the list for groups
     */
    private AntiFloodHandler() {
        groups = new ArrayList<>();
        isShuttingDown = false;
    }

    /**
     * @return instance of the class
     */
    public static AntiFloodHandler getInstance() {
        final AntiFloodHandler currentInstance;
        if (instance == null)
        {
            synchronized (AntiFloodHandler.class) {
                if (instance == null)
                {
                    instance = new AntiFloodHandler();
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
     * Updates antiflood settings for specific group
     * @param group id of the group to update (negative long)
     * @param messages number of messages for time slice (int > 0)
     * @param timeSlice limit time for the messages to be counted (int > 0, multiple of BuildVars.ANTIFLOODTIMESLICEMIN)
     * @return message with description if operation was successful
     */
    public StringBuilder updateAntiflood(Long group, int messages, int timeSlice) {
        try {
            String language = databaseManager.getGroupLanguage(group);
            if (!isShuttingDown) {
                databaseManager.updateGroupSettingsAntiflood(group, "true", timeSlice + "", messages + "");
                updateGroup(group);
                return new StringBuilder(Localizer.getString("done", language));
            }
            else
                return new StringBuilder(Localizer.getString("error_shutdown", language));
        } catch (SQLException e) {
            return new StringBuilder(Localizer.getString("error_update_settings", DEFAULT_LANG));
        }
    }

    /**
     * Starts or stops antiflood for selected group
     * @param group id of the group to update (negative long)
     * @param activated true\false to start or stop antiflood
     * @return message with description if operation was successful
     */
    public StringBuilder setAntiflood(Long group, String activated) {
        try {
            String language = databaseManager.getGroupLanguage(group);
            if (!isShuttingDown) {
                if (activated.equals("start")) {
                    databaseManager.updateGroupSettingsAntiflood(group, "true");
                    updateGroup(group);
                    return new StringBuilder(Localizer.getString("antiflood_active", language));
                } else if (activated.equals("stop")) {
                    databaseManager.updateGroupSettingsAntiflood(group, "false");
                    return new StringBuilder(Localizer.getString("antiflood_stopped", language));
                } else
                    return new StringBuilder(Localizer.getString("error", language));
            }
            else
                return new StringBuilder(Localizer.getString("error_shutdown", language));
        } catch (SQLException e) {
            return new StringBuilder(Localizer.getString("error_antiflood", DEFAULT_LANG));
        }
    }

    /**
     * Adds group to antiflood if it is present in DB
     * @param groupL id of the group to add (negative long)
     */
    private void addGroup(Long groupL) {
        String group = longtoString(groupL);
        int[] options = getAntifloodSettings(groupL);
        if (options!=null)
            groups.add(new GroupObject(group, options));
    }

    /**
     * Get antiflood settings for selected group from DB
     * @param groupL id of the group (negative long)
     * @return int[2] where int[0] = number of messages, int[1] = time slice
     */
    private int[] getAntifloodSettings(Long groupL) {
        String[] temp = databaseManager.getAntifloodSett(groupL);
        if (temp == null)
            return null;
        int[] ficc = new int[2];
        try {
            ficc[0] = Integer.parseInt(temp[0]);
            ficc[1] = Integer.parseInt(temp[1]);
            return ficc;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Updates group settings in Antiflood object retrieving them from DB
     * or adds the group if it does not exist
     * Fails if group is not in DB or if an error occurs
     * @param groupL id of the group (negative long)
     */
    private void updateGroup(Long groupL) {
        String group = longtoString(groupL);
        boolean exist = false;
        lock.lock();
        try {
            int[] options = getAntifloodSettings(groupL);
            if (options == null)
                return;
            for (GroupObject greap : groups)
            {
                if (greap.getID().equals(group))
                {
                    greap.setOptions(options);
                    exist = true;
                }
            }
            if (!exist)
                addGroup(groupL);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds 1 message to the count for selected user in selected group
     * @param groupL id of the group (negative long)
     * @param user id of the user (int)
     * @return true if user has sent more messages than those allowed, otherwise return false
     */
    public boolean addUserMessage(Long groupL, int user) {
        String group = longtoString(groupL);
        boolean risposta = false;
        lock.lock();
        try {
            if (!isShuttingDown) {
                for (GroupObject greap : groups) {
                    if (greap.getID().equals(group)) {
                        risposta = greap.addMessage(user);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
        return risposta;
    }

    /**
     * Increases messages age looping through groups and then users
     */
    public void execute() {
        lock.lock();
        if (!isShuttingDown) {
            try {
                for (GroupObject greap : groups) {
                    greap.increaseMessagesAge();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Stops antiflood (both insertion and aging of messages) waiting for it to be terminated
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
     * Class that represents a group
     * Keeps a lift of the users in this group
     * Note: all methods that change important variables
     * are called while inside a lock
     */
    private class GroupObject {
        private String groupID;
        //options[0] = number of messages
        //options[1] = timeSlice
        private int[] options;
        private ArrayList<UserObject> users;

        /**
         * Initialize an object
         * @param group id of the group (negative long to String)
         * @param options options for antiflood
         */
        private GroupObject(String group, int[] options) {
            groupID = group;
            this.options = options;
            users = new ArrayList<>();
        }

        /**
         * @return timeSlice
         */
        int getTimeSlice()
        {
            return options[1];
        }

        /**
         * @return id of the group
         */
        String getID() {
            return groupID;
        }

        /**
         * @return messages for timeslice allowed
         */
        int getMessages() {
            return options[0];
        }

        /**
         * Update options for chosen group
         * @param options new options for the group
         */
        void setOptions(int[] options)
        {
            this.options = options;
        }

        /**
         * adds a message to the count
         * @return true if message is in the limit of antiflood, false if it breaks the rule
         */
        boolean addMessage(int user)
        {
            lock.lock();
            try {
                int messageCount = 1;
                boolean newUser = true;
                for (UserObject uss : users)
                {
                    if (uss.getID() == user)
                    {
                        uss.addMessage();
                        uss.updateMessages(getTimeSlice());
                        messageCount = uss.getMessagesCount();
                        newUser = false;
                        break;
                    }
                }
                if (newUser)
                {
                    users.add(new UserObject(user));
                }
                return messageCount > getMessages();
            } finally {
                lock.unlock();
            }
        }

        /**
         * Called by execute to loop throw users to increase messages age
         */
        void increaseMessagesAge() {
            for (UserObject uss : users)
            {
                uss.updateMessages(getTimeSlice());
                if (uss.getMessagesCount() > 0)
                {
                    uss.increaseAge();
                }
            }
        }
    }

    /**
     * Class that represents a user
     * Keeps a lift of his messages
     * Note: all methods that change important variables
     * are called while inside a lock
     */
    private class UserObject {
        int user;
        private ArrayList<FloodMessage> messages;
        int messageCount;

        /**
         * Initialize a user
         * @param user id of the user
         */
        private UserObject(int user) {
            this.user = user;
            messages = new ArrayList<>();
            messageCount = 0;
        }

        /**
         * Adds a message to the list
         */
        private void addMessage() {
            messages.add(new FloodMessage());
            messageCount++;
        }

        /**
         * Check if a message is old enough to be removed
         * @param timeSlice timeslice for the group
         */
        private void updateMessages(int timeSlice) {
            if (messageCount > 0)
            {
                for (Iterator<FloodMessage> it = messages.iterator(); it.hasNext(); )
                {
                    FloodMessage mess = it.next();
                    if (mess.isOldMessage(timeSlice))
                    {
                        it.remove();
                        --messageCount;
                    }
                }
            }
        }

        /**
         * @return number of messages sent by this user in last timeslice
         */
        private int getMessagesCount() {
            return messageCount;
        }

        private void increaseAge() {
            if (messageCount > 0)
                for (FloodMessage mess : messages)
                {
                    mess.increaseTime();
                }
        }

        /**
         * @return id of the user
         */
        private int getID() {
            return user;
        }
    }

    /**
     * Class that represents a message
     */
    private class FloodMessage {

        private int timeSent = 0;

        /**
         * Initialize a message with age 0
         */
        private FloodMessage() {
            timeSent = 0;
        }

        /**
         * Increase message age of ANTIFLOODTIMESLICEMIN
         */
        private void increaseTime() {
            timeSent += ANTIFLOODTIMESLICEMIN;
        }

        /**
         * @param timeSlice for this group
         * @return true is message is older than timeSlice and should be removed
         */
        private boolean isOldMessage(int timeSlice)
        {
            return timeSent > timeSlice;
        }
    }
}
