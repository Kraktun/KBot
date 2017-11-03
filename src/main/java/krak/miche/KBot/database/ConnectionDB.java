
package krak.miche.KBot.database;

import krak.miche.KBot.BuildVars;
import krak.miche.KBot.services.UtilsMain;
import org.telegram.telegrambots.logging.BotLogger;

import java.sql.*;
import org.apache.commons.dbutils.DbUtils;

/**
 * @author Kraktun
 * @version 1.0
 * Class to manage the database
 */

class ConnectionDB {
    private static final String LOGTAG = "CONNECTIONDB";
    private Connection currentConnection;

    /**
     * Opens a connection to the DB
     */
    ConnectionDB() {
        this.currentConnection = openConnection();
    }

    /**
     * Connects to the DB
     * @return connection to the DB
     */
    private Connection openConnection() {
        Connection connection = null;
        try {
            Class.forName(BuildVars.controllerDB).newInstance();
            //For derby
            //Properties props = new Properties(); props.put("user", BuildVars.usernameDB);
            //props.put("password", BuildVars.password);
            //connection = DriverManager.getConnection(BuildVars.linkDB, props);
            connection = DriverManager.getConnection(BuildVars.linkDB);
            UtilsMain.log("Database Connected in ConnectionDB");
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            BotLogger.error(LOGTAG, e);
        }
        return connection;
    }

    /**
     * Closes connection to the DB
     */
    void closeConnection() {
        try {
            commitTransaction();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
        DbUtils.closeQuietly(currentConnection);
    }

    /**
     * Controls the version of the DB
     * @return version of the DB, 0 if it's first run
     */
    public int checkVersion() {
        int max = 0;
        try {
            ResultSet res = this.runSqlQuery(CreationStrings.LIST_INFO);
             while (res.next())
             {
                 String bb = res.getString("INFO");
                 if (bb.equals(CreationStrings.INFO_VERSION))
                 {
                     int tp = res.getInt("NUM");
                     res.close();
                     return tp;
                 }
             }
             res.close();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
            BotLogger.error(LOGTAG, "If it is first run, you can ignore this error");
        }
        return max;
    }

    /**
     * Initialize a transaction in database
     * @throws SQLException If initialization fails
     */
    public void initTransaction() throws SQLException {
        this.currentConnection.setAutoCommit(false);
    }

    /**
     * Finish a transaction in database and commit changes
     * @throws SQLException If a rollback fails
     */
    public void commitTransaction() throws SQLException {
        try {
            this.currentConnection.commit();
        } catch (SQLException e) {
            if (this.currentConnection != null)
            {
                this.currentConnection.rollback();
            }
        }
        finally {
            this.currentConnection.setAutoCommit(false);
        }
    }

    /**
     * Run a query
     * @param query query to run
     * @return ResultSet from the query
     * @throws SQLException if an error occurs
     */
    ResultSet runSqlQuery(String query) throws SQLException {
        final PreparedStatement statement = this.currentConnection.prepareStatement(query);
        try {
            return statement.executeQuery();
        }
        finally {
            statement.closeOnCompletion();
        }
    }

    /**
     * Run a query
     * @param query query to run
     * @param value first param (string) of the query
     * @return ResultSet from the query
     * @throws SQLException if an error occurs
     */
    ResultSet runSqlQuery(String query, String value) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        statement.setString(1, value);
        try {
            return statement.executeQuery();
        }
        finally {
            statement.closeOnCompletion();
        }
    }

    /**
     * Execute a query
     * @param query query to execute
     * @return execution result
     * @throws SQLException if an error occurs
     */
    Boolean executeQuery(String query) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        try {
            initTransaction();
            return statement.execute();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }

    Boolean executeQueryAttach(String query) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        try {
            return statement.execute();
        }
        finally {
            statement.close();
        }
    }

    /**
     * Execute an update
     * @param query query to execute
     * @param param1 first param to set
     * @return execution result
     * @throws SQLException if an error occurs
     */
    int executeUPD(String query, String param1) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        statement.setString(1, param1);
        try {
            initTransaction();
            return statement.executeUpdate();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }

    int executeUPD(String query, String param1, int param2) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        statement.setString(1, param1);
        statement.setInt(2,param2);
        try {
            initTransaction();
            return statement.executeUpdate();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }

    int executeUPD(String query, int param1, int param2) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        statement.setInt(1, param1);
        statement.setInt(2,param2);
        try {
            initTransaction();
            return statement.executeUpdate();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }

    int executeUPD(String query, int param1, String param2) throws SQLException {
        final PreparedStatement statement;
         statement = this.currentConnection.prepareStatement(query);
            statement.setInt(1, param1);
            statement.setString(2, param2);
        try {
            initTransaction();
            return statement.executeUpdate();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }

    int executeUPD(String query, String param1, String param2, String param3, String param4, String param5) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        statement.setString(1, param1);
        statement.setString(2,param2);
        statement.setString(3, param3);
        statement.setString(4,param4);
        statement.setString(5, param5);
        try {
            initTransaction();
            return statement.executeUpdate();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }

    int executeUPD(String query, String param1, String param2) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        statement.setString(1, param1);
        statement.setString(2,param2);
        try {
            initTransaction();
            return statement.executeUpdate();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }

    int executeUPD(String query, int param1) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        statement.setInt(1, param1);
        try {
            initTransaction();
            return statement.executeUpdate();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }

    int executeUPD(String query, String param1, int param2, String param3) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        statement.setString(1, param1);
        statement.setInt(2, param2);
        statement.setString(3, param3);
        try {
            initTransaction();
            return statement.executeUpdate();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }

    int executeUPD(String query, String param1, String param2, String param3, String param4) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        statement.setString(1, param1);
        statement.setString(2, param2);
        statement.setString(3, param3);
        statement.setString(4, param4);
        try {
            initTransaction();
            return statement.executeUpdate();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }

    int executeUPD(String query, int param1, String param2, String param3, String param4) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        statement.setInt(1, param1);
        statement.setString(2, param2);
        statement.setString(3, param3);
        statement.setString(4, param4);
        try {
            initTransaction();
            return statement.executeUpdate();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }

    int executeUPD(String query, int param1, String param2, String param3, String param4, int param5, String param6) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        statement.setInt(1, param1);
        statement.setString(2, param2);
        statement.setString(3, param3);
        statement.setString(4, param4);
        statement.setInt(5, param5);
        statement.setString(6, param6);
        try {
            initTransaction();
            return statement.executeUpdate();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }

    int executeUPD(String query, String param1, int param2, String param3, String param4, String param5) throws SQLException {
        final PreparedStatement statement;
        statement = this.currentConnection.prepareStatement(query);
        statement.setString(1, param1);
        statement.setInt(2, param2);
        statement.setString(3, param3);
        statement.setString(4, param4);
        statement.setString(5, param5);
        try {
            initTransaction();
            return statement.executeUpdate();
        }
        finally {
            statement.close();
            commitTransaction();
        }
    }
}
