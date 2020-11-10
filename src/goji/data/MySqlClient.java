package goji.data;

import goji.common.GojiLogManagement;
import java.sql.SQLException;
import java.util.logging.*;

/** Client class that handles all operations between this project and MySql
 *  utilizing JDBC for MySql.
 *
 *  @author Aaron
 *  @version 20210.26
 */
public class MySqlClient extends SqlClient {

    private static final Logger LOGGER = GojiLogManagement.createLogger(MySqlClient.class.getName());

    /** Create a new TaskDbSchema object.
     *
     * @param dbConfigWrapper the configuration need to authenticate to the db
     */
    public MySqlClient(DatabaseConfigWrapper dbConfigWrapper) {
        super(ConnectionOperations.connectToDbAccount(dbConfigWrapper));
    }

    /** Creates a MySql database
     *
     * @param database the database set as default to work on
     * @return true if the default connection was successful
     */
    public boolean setDefaultDatabaseCreateIfNotExists(String database) {

        LOGGER.info("Creating database...");

        super.updateDatabaseStatement("CREATE DATABASE IF NOT EXISTS " + database);
        try {
            super.conn.setCatalog(database);
        }
        catch (SQLException se) {
            LOGGER.severe("Failed to connect to database" + se.toString());
            return false;
        }

        LOGGER.info("Creation successful with no issue...");

        return true;
    }

    /** Method that deletes a given database
     *
     */
    public void deleteDb(String database) {
        LOGGER.info("Delete database...");

        super.updateDatabaseStatement("DROP DATABASE " + database);

        LOGGER.info("Database deleted successfully, db name: " + database);
    }

}
