package krak.miche.KBot.secondary_Handler;


import krak.miche.KBot.database.DatabaseManager;

/**
 * @author Kraktun
 * @version 1.0
 */

public class CheckStatusHandler {
    public static final String LOGTAG = "CHECKSTATUSHANDLER";
    private DatabaseManager databaseManager = DatabaseManager.getInstance();

    public CheckStatusHandler(){
    }

    public boolean isBlacklisted(Long groupID, int user){
        return databaseManager.isUserGroupBlacklisted(groupID, user);
    }
}
