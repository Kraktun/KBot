package krak.miche.KBot.database;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * @author Kraktun
 * @version 1.0
 * Helper class for database operations (only sqlite)
 */
public class SQLUtil {

    private static final String LOGTAG = "SQLUtil";

    private static volatile DatabaseManager databaseManager = DatabaseManager.getInstance();


    public static boolean elementAlreadyExists(String table, String column, String value) throws SQLException{
        String selectQuery = replace(CreationStrings.GENERIC_SEARCH, table);
        selectQuery = replace(selectQuery, column);
        ResultSet resultSet = databaseManager.runCustomSQL(selectQuery, value);
        if (resultSet != null && resultSet.getString(column)!=null && resultSet.getString(column).equals(value)) {
            return true;
        }
        return false;
    }

    public static boolean elementAlreadyExists(SQLException e ){
        return e.getErrorCode()==19;
    }

    public static boolean genericError(SQLException e ){
        return e.getErrorCode()==1;
    }

    /**
     * Replaces the first # with the param and returns the resulting query
     *
     * @param query query with # to replace
     * @param param1 param to insert
     * @return query with the first # replaced with param1
     */
    private static String replace(String query, String param1)
    {
        Scanner in = new Scanner (query);
        in.useDelimiter("#");
        String firstHalf = in.next();
        String secondHalf = in.nextLine();
        in.close();
        secondHalf = secondHalf.substring(1, secondHalf.length());
        return firstHalf +  param1 + secondHalf;
    }

    /**
     * Converts a long to a string without -
     * @param id negative long
     * @return string containing the absolute value of that long
     */
    public static String longtoString(Long id)
    {
        if (id<0)
            return Math.abs(id) + "";
        return id + "";
    }
}
