package krak.miche.KBot.database;

import java.sql.SQLException;

/**
 * @author Kraktun
 * @version 1.0
 * Helper class for database operations
 */
public class DerbyUtils {

    public DerbyUtils() {
        //empty constructor -- helper class
    }

    public static boolean elementAlreadyExists(SQLException e) {
        return e.getSQLState().equals("X0Y32");
    }

    public static boolean schemaAlreadyExists(SQLException e) {
        return e.getSQLState().equals("X0Y68");
    }

    public static boolean tableNotExist(SQLException e) {
        return e.getSQLState().equals("42X05");
    }

    public static boolean tableNotDroppable(SQLException e) {
        return e.getSQLState().equals("42Y55");
    }

    public static String longtoString(Long id)
    {
        String tt = id + "";
        if (tt.length() > 1 && tt.substring(0,1).equals("-"))
            return tt.substring(1,tt.length());
        return tt;
    }
}
