package goji.task;

import goji.common.GojiLogManagement;
import goji.common.utils.StringUtils;
import goji.data.TaskDbSchema.TaskTable;
import goji.data.MySqlClient;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/** Data Access Object for database operations on a Task class
 *
 * @author Aaron
 * @version 2020.11.07
 */
public class TaskCursor implements ITaskCursor {
    private static final Logger LOGGER =
        GojiLogManagement.createLogger(TaskCursor.class.getName());
    private MySqlClient mySqlClient;
    private TaskEntryCursor taskEntryCursor;


    /** Method for constructing a TaskCursor object
     * @param mySqlClient the Client we run SQL operations on
     * @param taskEntryCursor the correlated TaskEntryCursor to run operations on
     *
     */
    TaskCursor(MySqlClient mySqlClient, TaskEntryCursor taskEntryCursor) {
        this.mySqlClient = mySqlClient;
        this.taskEntryCursor = taskEntryCursor;
    }

    /** Gets a specific Task from the database with a unique id
    *
    * @param uuid the unique id used to retrieve the task
    * @return the Task requested
    */
    public Task getTask(String uuid) {
        LOGGER.info("Querying Task with uuid: " + uuid);
        String query = StringUtils.applyFormat(
            "SELECT * FROM {0} WHERE uuid = {1}",
            TaskTable.NAME,
            uuid);
        ResultSet result = mySqlClient
            .queryDatabaseStatement(query);
        Statement stmt = null;

        String uuidString = "";
        String title = "";
        long date = 0;
        int isDeferred = 0;
        int isRealized = 0;

        try {
            while (result.next()) {
                uuidString = result.getString(TaskTable.Cols.UUID);
                title = result.getString(TaskTable.Cols.TITLE);
                date = result.getLong(TaskTable.Cols.DATE);
                isDeferred = result.getInt(TaskTable.Cols.DEFERRED);
                isRealized = result.getInt(TaskTable.Cols.REALIZED);
            }
            stmt = result.getStatement();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, result, LOGGER);
        }

        LOGGER.info(
            StringUtils.applyFormat(
                "Task retrieved... uuid: {0}, title: {1}, date: {2}, deferredState: {3}, realizedSate: {4}",
                uuidString,
                title,
                Long.toString(date),
                Integer.toString(isDeferred),
                Integer.toString(isRealized)));

        Task task = new Task(UUID.fromString(uuidString));
        task.setTitle(title);
        task.setRevealedDate(new Date(date));
        task.setDeferred(isDeferred != 0);
        task.setRealized(isRealized != 0);

        return task;
    }

    /** Retrieves all Tasks in the database
    *
    * @return the list of Tasks in the database
    */
    public List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();

        LOGGER.info("Querying all Tasks for the given user...");
        String query =
            StringUtils.applyFormat(
                "SELECT * FROM {0}" +
                "ORDER BY id ASC",
                TaskTable.NAME);
        ResultSet result = mySqlClient
            .queryDatabaseStatement(query);
        Statement stmt = null;


        try {
            while (result.next()) {
                String taskId = result.getString(TaskTable.Cols.UUID);
                Task task = new Task(UUID.fromString(taskId));
                task.setTitle(result.getString(TaskTable.Cols.TITLE));
                task.setRevealedDate(
                    new Date(result.getLong(TaskTable.Cols.DATE)));
                task.setDeferred(result.getInt(TaskTable.Cols.DEFERRED) != 0);
                task.setRealized(result.getInt(TaskTable.Cols.REALIZED) != 0);
                LOGGER.info(
                    StringUtils.applyFormat(
                        "Retrieved Task with UUID: {0}, returning it with List of Tasks",
                        taskId));
                taskList.add(task);
            }
            stmt = result.getStatement();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, result, LOGGER);
        }

        return taskList;
    }


    /** Adds a new Task
    *
    * @param task the new Task that gets added
    */
    public void addTask(Task task) {
        LOGGER.info("Adding Task with uuid: " + task.getId().toString());

        String insert = StringUtils.applyFormat(
            "INSERT INTO {0} " + "VALUES(?,?,?,?,?,?)",
            TaskTable.NAME);

        // add the Task
        PreparedStatement stmt = null;
        try {

            stmt = mySqlClient.getPreparedStatement(insert);
            stmt.setString(0, task.getId().toString());
            stmt.setString(1, task.getTitle());
            stmt.setLong(2, task.getRevealedDate().getTime());
            stmt.setInt(3, task.isDeferred() ? 1 : 0);
            stmt.setInt(4, task.isRealized() ? 1 : 0);
            stmt.setString(5, task.getPhotoFilename());

            stmt.executeUpdate();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, null, LOGGER);
        }

        for (TaskEntry entry: task.getTaskEntries()) {
            taskEntryCursor.addTaskEntry(entry, task);
        }

        LOGGER.info("Task added to the database...");
    }


    /** Delete a Task
    *
    * @param task the Task that gets deleted
    */
    public void deleteTask(Task task) {
        LOGGER.info("Deleteing Task with uuid: " + task.getId().toString());

        String delete = StringUtils.applyFormat(
            "DELETE FROM {0} WHERE uuid = {1}",
            TaskTable.NAME,
            task.getId().toString());
        // delete the Task
        mySqlClient.updateDatabaseStatement(delete);

        LOGGER.info("Task deleted from the database...");
    }

    /** Update a Task
    *
    * @param task the Task that gets updated
    */
    void updateTask(Task task) {
        LOGGER.info("Updating Task with uuid: " + task.getId().toString());

        String insert = StringUtils.applyFormat(
            "UPDATE {0} "
                + "SET {1}=?, {2}=?, {3}=?, {4}=? "
                + "WHERE {5}=?",
            TaskTable.NAME,
            TaskTable.Cols.TITLE,
            TaskTable.Cols.DEFERRED,
            TaskTable.Cols.REALIZED,
            TaskTable.Cols.PHOTOPATH,
            TaskTable.Cols.UUID);

        // update the Task
        PreparedStatement stmt = null;
        try {
            stmt = mySqlClient.getPreparedStatement(insert);
            stmt.setString(1, task.getTitle());
            stmt.setInt(2, task.isDeferred() ? 1 : 0);
            stmt.setInt(3, task.isRealized() ? 1 : 0);
            stmt.setString(4, task.getPhotoFilename());
            stmt.setString(5, task.getId().toString());

            stmt.executeUpdate();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, null, LOGGER);
        }

        LOGGER.info("Task updated in database...");
    }
}
