package goji.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/** Houses common connection operations done on a database in this project
 *
 *  @author Aaron
 *  @version Oct 27, 2020
 */
public class ConnectionOperations {
    private DatabaseConfigWrapper dbConfig;

    /** Create a new ConnectionOperations object with a set access level of default
     *  to only allow classes in this package to create new ConnectionOperations
     *  objects.
     *
     * @param dbConfig the MySql db configuration needed to create a connection
     */
    ConnectionOperations(DatabaseConfigWrapper dbConfig) {
        this.dbConfig = dbConfig;
    }

    /** Creates a connection to the requested MySql DB
     *
     * @param logger the Logger passed in by requesting class
     * @return the connection to the MySql DB
     */
    Connection connectToDb(Logger logger) {
        Connection result = null;
        try {
            //register JDBC driver
            Class.forName(dbConfig.getJdbcDriver());

            //open a connection
            logger.info("Connecting to database...");
            result = DriverManager.getConnection(dbConfig.getMysqlUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        }
        catch(SQLException se) {
            logger.warning("There is no error... " + se.toString());
        }
        catch(Exception e) {
            logger.severe(e.toString());
        }

        return result;
    }

    /** Closes the MySql Connection and Statement from a given class
     *
     * @param conn the connection to close
     * @param stmt the SQL statement to close
     * @param rs the ResultSet that will be closed
     * @param logger the Logger passed in by requesting class
     */
    void closeDbConnection(Connection conn, Statement stmt, ResultSet rs, Logger logger) {
        try {
            if(rs != null) {
                rs.close();
                logger.info("ResultSet closed successfully...");
            }
        }
        catch(SQLException se2) {
            logger.warning(se2.toString());
        }
        try {
            if(stmt != null) {
                stmt.close();
                logger.info("SQL statement closed successfully...");
            }
        }
        catch(SQLException se2) {
            logger.warning(se2.toString());
        }
        try {
            if(conn != null) {
                conn.close();
                logger.info("Connection to db closed successfully...");
            }
        }
        catch(SQLException se) {
            logger.warning(se.toString());
        }
    }

}
