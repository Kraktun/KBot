package krak.miche.KBot.structures;

/**
 * @author Kraktun
 * @version 1.0
 */

public class LogObject {

    private long groupID;
    private int userID;
    private String username;
    private String date;
    private String message;

    public LogObject(long groupID, int userID, String username, String date, String message) {
        this.groupID = groupID;
        this.userID = userID;
        this.username = username;
        this.date = date;
        this.message = message;
    }

    public long getGroupID() {
        return groupID;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }
}
