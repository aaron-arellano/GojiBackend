package goji.data;

import goji.common.GojiLogManagement;
import goji.common.utils.StringUtils;
import goji.data.TaskDbSchema.TaskEntryTable;
import goji.data.TaskDbSchema.TaskTable;
import java.util.logging.Logger;

/** Creates the tables needed for the database in this project. This is the gateway
 *  or client class used for this project, leveraging the other classes to create
 *  the db and associated entities.
 *
 *  @author Aaron
 *  @version 2020.10.31
 */
public class TaskDbHelper {
    private static final Logger LOGGER = GojiLogManagement.createLogger(TaskDbHelper.class.getName());
    private MySqlClient mySqlClient;

    /** Create a new TaskDbHelper object.
     *
     * @param mySqlClient
     */
    public TaskDbHelper(MySqlClient mySqlClient) {
        this.mySqlClient = mySqlClient;
    }

    /** Create the tables needed for this database
     *
     */
    public void createDbCreateTable() {

        LOGGER.info("Creating table: " + TaskTable.NAME);
        String stmt = StringUtils.applyFormat("CREATE TABLE IF NOT EXISTS {0} ( "
            + "id INT AUTO_INCREMENT PRIMARY KEY, "
            + "{1} VARCHAR(30) NOT NULL UNIQUE, "
            + "{2} VARCHAR(20), "
            + "{3} DATE, "
            + "{4} INT, "
            + "{5} INT, "
            + "{6} VARCHAR(100) )",
            TaskTable.NAME, TaskTable.Cols.UUID, TaskTable.Cols.TITLE, TaskTable.Cols.DATE,
            TaskTable.Cols.DEFERRED, TaskTable.Cols.REALIZED, TaskTable.Cols.PHOTOPATH);

        mySqlClient.updateDatabaseStatement(stmt);
        LOGGER.info("Table exists: " + TaskTable.NAME);

        LOGGER.info("Creating table: " + TaskEntryTable.NAME);
        stmt = StringUtils.applyFormat("CREATE TABLE IF NOT EXISTS {0} ( "
            + "id INT AUTO_INCREMENT PRIMARY KEY, "
            + "{1} VARCHAR(300), "
            + "{2} DATE, "
            + "{3} VARCHAR(30), "
            + "{4} VARCHAR(30) NOT NULL UNIQUE, "
            + "{5} VARCHAR(30) NOT NULL UNIQUE, "
            + "FOREIGN KEY ({6}) REFERENCES {7} ({8}) )",
            TaskEntryTable.NAME, TaskEntryTable.Cols.TEXT, TaskEntryTable.Cols.DATE,
            TaskEntryTable.Cols.KIND, TaskEntryTable.Cols.ENTRYID, TaskEntryTable.Cols.UUID,
            TaskEntryTable.Cols.UUID, TaskTable.NAME, TaskTable.Cols.UUID);

        mySqlClient.updateDatabaseStatement(stmt);
        LOGGER.info("Table exists: " + TaskEntryTable.NAME);
    }

}
