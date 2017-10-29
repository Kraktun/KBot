package krak.miche.KBot.database;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

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
     * Sostituisce il PRIMO # con un parametro passato e restituisce il query completo
     *
     * @param query Stringa da analizzare
     * @param param1 primo # da sostituire
     * @return stringa con il primo # sostituito con param1
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

    public static String longtoString(Long id)
    {
        String tt = id + "";
        if (tt.length() > 1 && tt.substring(0,1).equals("-"))
            return tt.substring(1,tt.length());
        return tt;
    }
}
