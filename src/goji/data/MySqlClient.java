package goji.data;

import goji.common.GojiLogManagement;
import java.sql.*;
import java.util.logging.*;

/** Client class that handles all operations between this project and MySql
 *  utilizing JDBC for MySql.
 *
 *  @author Aaron
 *  @version 20210.26
 */
public class MySqlClient {

    private static final Logger LOGGER = GojiLogManagement.createLogger(MySqlClient.class.getName());
    private ConnectionOperations connectionOperations;
    private DatabaseConfigWrapper dbConfigWrapper;
    private Connection conn;

    /** Create a new TaskDbSchema object.
     *
     * @param dbConfigWrapper the configuration need to authenticate to the db
     */
    public MySqlClient(DatabaseConfigWrapper dbConfigWrapper) {
        this.dbConfigWrapper = dbConfigWrapper;
        connectionOperations = new ConnectionOperations(this.dbConfigWrapper);
        conn = connectionOperations.connectToDb(LOGGER);
        setDefaultDatabase();
    }

    /** Creates a MySql database
     *
     * @return true if successful
     */
    public boolean createDB() {
        Statement stmt = null;
        try {

            LOGGER.info("Creating database...");
            stmt = executeSqlUpdate("CREATE DATABASE IF NOT EXISTS " + dbConfigWrapper.getDatabaseName());

            // only get to this point if the db does not exist
            LOGGER.warning(stmt.getWarnings().getMessage());
        }
        catch(SQLException se) {
            LOGGER.warning(se.toString());
            return false;
        }
        catch(NullPointerException npe) {
            LOGGER.info("Database created successfully, db name: " + dbConfigWrapper.getDatabaseName());
        }
        catch(Exception e) {
            LOGGER.severe(e.toString());
            return false;
        }
        finally {
            connectionOperations.closeDbConnection(null, stmt, null, LOGGER);
        }
        LOGGER.info("Creation successful with no issue...");
        return true;
    }

    /** Method that deletes a given database
     *
     * @return true if the database delete was successful
     */
    public boolean deleteDB() {
        Statement stmt = null;
        try {

            LOGGER.info("Delete database...");
            stmt = executeSqlUpdate("DROP DATABASE " + dbConfigWrapper.getDatabaseName());
            LOGGER.info("Database deleted successfully, db name: " + dbConfigWrapper.getDatabaseName());
        }
        catch(SQLException se) {
            LOGGER.warning(se.toString());
            return false;
        }
        catch(Exception e) {
            LOGGER.severe(e.toString());
            return false;
        }
        finally {
            connectionOperations.closeDbConnection(null, stmt, null, LOGGER);
        }
        LOGGER.info("Deletion successful with no issue...");
        return true;
    }

    /** Public method to update SQL database
     *
     * @param stringStatement the input SQL statement to execute
     */
    public void updateDatabaseStatement(String stringStatement) {
        Statement stmt = null;
        try {

            LOGGER.info("Update statement: " + stringStatement);
            stmt = executeSqlUpdate(stringStatement);
        }
        catch(SQLException se) {
            LOGGER.warning(se.toString());
        }
        catch(Exception e) {
            LOGGER.severe(e.toString());
        }
        finally {
            connectionOperations.closeDbConnection(null, stmt, null, LOGGER);
        }
        LOGGER.info("Database successfully updated...");
    }

    /** Public method to query SQL database. Classes that use this method must close the db
     *  Connection, Statement, or ResultSet utilizing the ConnectionOperations
     *  GET reference provided by this class.
     *
     * @param stringStatement the input SQL statement to execute
     * @return the result set containing SQL results from query
     */
    public ResultSet queryDatabaseStatement(String stringStatement) {
        ResultSet result = null;
        Statement stmt = null;
        try {

            LOGGER.info("Query statement: " + stringStatement);
            stmt = executeSqlQuery(stringStatement);
            result = stmt.getResultSet();
        }
        catch(SQLException se) {
            LOGGER.warning(se.toString());
        }
        catch(Exception e) {
            LOGGER.severe(e.toString());
        }

        LOGGER.info("Database query successful, ResultSet returned to consumer...");
        return result;
    }

    /** Wrap method invocation of a prepared statement in this class to control
     *  instantiation. Classes that use this method must close the PreparedStatement
     *
     * @param queryStmt the String query given to the database
     * @return the PreparedStatement object used by other classes
     * @throws SQLException
     */
    public PreparedStatement getPreparedStatement(String queryStmt) throws SQLException {
        return conn.prepareStatement(queryStmt);
    }

    /** Expose method to be able to close Statements, and ResultSets
     *  to other objects outside of this scope
     *
     * @param stmt Statement to close
     * @param result ResultSet to close
     * @param logger Logger to log status of closure
     */
    public void closeStatementResultSet(Statement stmt, ResultSet result, Logger logger) {
        connectionOperations.closeDbConnection(null, stmt, result, logger);
    }


    /** Expose method to close database Connections for this client. This should
     *  only be called when application is done with the database
     *
     * @param logger Logger to log status of closure
     */
    public void closeConnection(Logger logger) {
        connectionOperations.closeDbConnection(conn, null, null, logger);
    }

    private Statement executeSqlUpdate(String stringStatement) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = stringStatement;
        stmt.executeUpdate(sql);

        return stmt;
    }

    private Statement executeSqlQuery(String stringStatement) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = stringStatement;
        stmt.executeQuery(sql);

        return stmt;
    }

    private void setDefaultDatabase() {
        try {
            LOGGER.info("Setting default database to: " + dbConfigWrapper.getDatabaseName());
            conn.setCatalog(dbConfigWrapper.getDatabaseName());
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
    }

}
