package goji.task;

import goji.common.GojiLogManagement;
import goji.common.utils.StringUtils;
import goji.data.MySqlClient;
import goji.data.TaskDbSchema.TaskEntryTable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/** Data Access Object worker for a TaskEntry
 *
 *  @author Aaron
 *  @version 2020.11.07
 */
public class TaskEntryCursor implements ITaskEntryCursor{
    private static final Logger LOGGER =
        GojiLogManagement.createLogger(TaskEntryCursor.class.getName());
    private MySqlClient mySqlClient;

    /** Construct a TaskEntryCursor object
     *
     * @param mySqlClient the Client we run SQL operations on
     */
    TaskEntryCursor(MySqlClient mySqlClient) {
        this.mySqlClient = mySqlClient;
    }

    /** Adds a Task entry to the given Task
     *
     * @param taskEntry the task entry getting added
     * @param task the given Task to update
     */
    public void addTaskEntry(TaskEntry taskEntry, Task task) {
        LOGGER.info("Adding TaskEntry with uuid: " + taskEntry.getTaskEntryID().toString());

        String insert = StringUtils.applyFormat(
            "INSERT INTO {0} " + "VALUES(?,?,?,?,?)",
            TaskEntryTable.NAME);

        // add the TaskEntry
        PreparedStatement stmt = null;
        try {
            stmt = mySqlClient.getPreparedStatement(insert);
            stmt.setString(0, taskEntry.getEntryText());
            stmt.setLong(1, taskEntry.getEntryDate().getTime());
            stmt.setString(2, taskEntry.getTaskEntryKind().name());
            stmt.setString(3, taskEntry.getTaskEntryID().toString());
            stmt.setString(4, task.getId().toString());

            stmt.executeUpdate();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, null, LOGGER);
        }

        LOGGER.info("TasEntry added to the database...");
    }


    /** Updates a TaskEntry
     *
     * @param taskEntry the entry getting updated
     */
    public void updateTaskEntryText(TaskEntry taskEntry) {
        LOGGER.info("Updating Task with uuid: " + taskEntry.getTaskEntryID().toString());

        String insert = StringUtils.applyFormat(
            "UPDATE {0} "
                + "SET {1}=?, {2}=?, {3}=? "
                + "WHERE {4}=?",
            TaskEntryTable.NAME,
            TaskEntryTable.Cols.TEXT,
            TaskEntryTable.Cols.DATE,
            TaskEntryTable.Cols.KIND,
            TaskEntryTable.Cols.ENTRYID);

        // update the Task
        PreparedStatement stmt = null;
        try {
            // TODO
            stmt = mySqlClient.getPreparedStatement(insert);
            stmt.setString(0, taskEntry.getEntryText().toString());
            stmt.setLong(1, taskEntry.getEntryDate().getTime());
            stmt.setString(2, taskEntry.getTaskEntryKind().name());
            stmt.setString(3, taskEntry.getTaskEntryID().toString());

            stmt.executeUpdate();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, null, LOGGER);
        }

        LOGGER.info("TaskEntry updated in database...");
    }

    /** Returns a list of Task Entries from the MySql database
     *
     * @param task the task we will get the entries from
     * @return the list of TaskEntries from the given Task
     */
    public List<TaskEntry>getTaskEntries(Task task) {
        List<TaskEntry> taskEntries = new ArrayList<>();
        String taskId = task.getId().toString();

        LOGGER.info("Querying all TaskEntries for Task with uuid: " + taskId);
        String query =
            StringUtils.applyFormat(
                "SELECT * FROM {0}" +
                "WHERE {1} = {2}" +
                "ORDER BY id ASC",
                TaskEntryTable.NAME,
                TaskEntryTable.Cols.ENTRYID,
                taskId);
        ResultSet result = mySqlClient
            .queryDatabaseStatement(query);
        Statement stmt = null;

        try {
            while (result.next()) {
                String entryId = result.getString(TaskEntryTable.Cols.ENTRYID);
                TaskEntry taskEntry = new TaskEntry(UUID.fromString(entryId));
                taskEntry.setEntryText(result.getString(TaskEntryTable.Cols.TEXT));
                taskEntry.setEntryDate(new Date(result.getLong(TaskEntryTable.Cols.DATE)));
                taskEntry.setTaskEntryKind(TaskEntryKind.valueOf(result.getString(TaskEntryTable.Cols.KIND)));

                LOGGER.info(
                    StringUtils.applyFormat(
                        "Retrieved TaskEntry with UUID: {0}, returning it with List of Tasks",
                        entryId));
                taskEntries.add(taskEntry);
            }
            stmt = result.getStatement();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, result, LOGGER);
        }

        return taskEntries;
    }
}
