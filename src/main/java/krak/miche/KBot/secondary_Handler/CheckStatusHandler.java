package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;

/**
 * @author Kraktun
 * @version 1.0
 */

public class CheckStatusHandler {
    public static final String LOGTAG = "CHECKSTATUSHANDLER";

    public static boolean isBlacklisted(Long groupID, int user){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        return databaseManager.isUserGroupBlacklisted(groupID, user);
    }
}
