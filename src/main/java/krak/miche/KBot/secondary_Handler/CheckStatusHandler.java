package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;

/**
 * @author Kraktun
 * @version 1.0
 */

public class CheckStatusHandler {
    public static final String LOGTAG = "CHECKSTATUSHANDLER";

    /**
     * Check if user's status in DB is 'BLACKLISTED' for chosen group
     * @param groupID group where you want to check the user's status
     * @param user id of the user
     * @return true if user is blacklisted, false otherwise
     */
    public static boolean isBlacklisted(Long groupID, int user){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        return databaseManager.isUserGroupBlacklisted(groupID, user);
    }
}
