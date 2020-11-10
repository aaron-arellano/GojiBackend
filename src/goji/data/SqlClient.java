package goji.data;

import goji.common.GojiLogManagement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/** Abstract superclass which holds common methods by any other *SqlClient to use
 *  and extends method definition for sub classes to implement different create and
 *  delete operations for the Sql database type
 *
 *  @author Aaron
 *  @version 2020.11.09
 */
public abstract class SqlClient {

    private static final Logger LOGGER = GojiLogManagement.createLogger(SqlClient.class.getName());
    /** Connection parameter for any SqlClient concrete implementations to utilize.
     *  There is one connection with one SQLClient.
     *
     */
    protected Connection conn;

    /** Create a new SqlClient object.
     *
     * @param conn the connection for the concrete class calling this parent
     */
    protected SqlClient(Connection conn) {
        this.conn = conn;
    }

    @SuppressWarnings("javadoc")
    public abstract boolean setDefaultDatabaseCreateIfNotExists(String database);

    @SuppressWarnings("javadoc")
    public abstract void deleteDb(String database);

    /** Public method to update SQL database
     *
     * @param stringStatement the input SQL statement to execute
     */
    public void updateDatabaseStatement(String stringStatement) {
       Statement stmt = null;
       try {

           LOGGER.info("Update statement: " + stringStatement);
           stmt = conn.createStatement();
           stmt.executeUpdate(stringStatement);
       }
       catch(SQLException se) {
           LOGGER.warning(se.toString());
       }
       catch(Exception e) {
           LOGGER.severe(e.toString());
       }
       finally {
           ConnectionOperations.closeDbConnections(null, stmt, null);
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
           stmt = conn.createStatement();
           stmt.executeQuery(stringStatement);

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
     */
    public void closeStatementResultSet(Statement stmt, ResultSet result) {
        ConnectionOperations.closeDbConnections(null, stmt, result);
    }

    /** Expose method to close database Connections for this client. This should
     *  only be called when application is done with the database
     *
     */
    public void closeConnection() {
        ConnectionOperations.closeDbConnections(conn, null, null);
    }

}
