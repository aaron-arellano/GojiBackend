package goji.data;

import goji.common.GojiLogManagement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/** Houses common connection operations done on a database in this project
 *
 *  @author Aaron
 *  @version 2020.11.09
 */
public class ConnectionOperations {

    private static final Logger LOGGER = GojiLogManagement.createLogger(ConnectionOperations.class.getName());

    /** Default constructor
     *
     */
    ConnectionOperations() {
        //
    }

    /** Creates a connection to the requested MySql DB
     *
     * @param dbConfig configuration used to connect to database
     *
     * @return the connection to the MySql DB
     */
    static Connection connectToDbAccount(DatabaseConfigWrapper dbConfig) {
        Connection result = null;
        try {
            //register JDBC driver
            Class.forName(dbConfig.getJdbcDriver());

            //open a connection
            LOGGER.info("Connecting to database...");
            result = DriverManager.getConnection(dbConfig.getMysqlUrl(), dbConfig.getUsername(), dbConfig.getPassword());

            LOGGER.info("Setting default database to: " + dbConfig.getDatabaseName());
        }
        catch(SQLException se) {
            LOGGER.warning("There is no error... " + se.toString());
        }
        catch(Exception e) {
            LOGGER.severe(e.toString());
        }

        return result;
    }

    /** Closes the MySql Connection and Statement from a given class
     *
     * @param conn the connection to close
     * @param stmt the SQL statement to close
     * @param rs the ResultSet that will be closed
     */
    static void closeDbConnections(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if(rs != null) {
                rs.close();
                LOGGER.info("ResultSet closed successfully...");
            }
        }
        catch(SQLException se2) {
            LOGGER.warning(se2.toString());
        }
        try {
            if(stmt != null) {
                stmt.close();
                LOGGER.info("SQL statement closed successfully...");
            }
        }
        catch(SQLException se2) {
            LOGGER.warning(se2.toString());
        }
        try {
            if(conn != null) {
                conn.close();
                LOGGER.info("Connection to db closed successfully...");
            }
        }
        catch(SQLException se) {
            LOGGER.warning(se.toString());
        }
    }

}
