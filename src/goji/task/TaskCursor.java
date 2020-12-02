package goji.task;

import goji.common.GojiLogManagement;
import goji.common.utils.StringUtils;
import goji.data.TaskDbSchema.TaskTable;
import goji.data.SqlClient;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.ws.rs.NotFoundException;

/** Data Access Object for database operations on a Task class
 *
 * @author Aaron
 * @version 2020.11.17
 */
public class TaskCursor implements ITaskCursor {
    private static final Logger LOGGER =
        GojiLogManagement.createLogger(TaskCursor.class.getName());
    private SqlClient mySqlClient;


    /** Method for constructing a TaskCursor object
     *
     * @param mySqlClient the Client we run SQL operations on
     */
    public TaskCursor(SqlClient mySqlClient) {
        this.mySqlClient = mySqlClient;
    }

    /** Gets a specific Task from the database with a unique id
    *
    * @param uuid the unique id used to retrieve the task
    * @return the Task requested
    */
    public Task getTask(String uuid) {
        LOGGER.info("Querying Task with uuid: " + uuid);
        String query = StringUtils.applyFormat(
            "SELECT * FROM {0} WHERE task_uuid = \"{1}\"",
            TaskTable.NAME,
            uuid);
        ResultSet result = mySqlClient
            .queryDatabaseStatement(query);
        Statement stmt = null;
        int rows = 0;

        String uuidString = "";
        String title = "";
        String photoPath = "";
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
                photoPath = result.getString(TaskTable.Cols.PHOTOPATH);
                rows++;
            }
            stmt = result.getStatement();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, result);
        }
        
        if (rows == 0 ) {
        	throw new NotFoundException("task: "+ uuid +" was not found, get task failed.");
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
        task.setTaskTitle(title);
        task.setTaskRevealedDate(new Date(date));
        task.setTaskDeferred(isDeferred != 0);
        task.setTaskRealized(isRealized != 0);
        //task.setTaskEntries(null);
        task.setPhotoFilePath(photoPath);

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
                "SELECT * FROM {0} " +
                "ORDER BY id ASC",
                TaskTable.NAME);
        ResultSet result = mySqlClient
            .queryDatabaseStatement(query);
        Statement stmt = null;


        try {
            while (result.next()) {
                String taskId = result.getString(TaskTable.Cols.UUID);
                Task task = new Task(UUID.fromString(taskId));
                task.setTaskTitle(result.getString(TaskTable.Cols.TITLE));
                task.setTaskRevealedDate(
                    new Date(result.getLong(TaskTable.Cols.DATE)));
                task.setTaskDeferred(result.getInt(TaskTable.Cols.DEFERRED) != 0);
                task.setTaskRealized(result.getInt(TaskTable.Cols.REALIZED) != 0);
                task.setPhotoFilePath(result.getString(TaskTable.Cols.PHOTOPATH));
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
            mySqlClient.closeStatementResultSet(stmt, result);
        }

        return taskList;
    }


    /** Adds a new Task
    *
    * @param task the new Task that gets added
    */
    public void addTask(Task task) {
        LOGGER.info("Adding Task with uuid: " + task.getTaskId().toString());

        String insert = StringUtils.applyFormat(
            "INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}) " + "VALUES(?,?,?,?,?,?)",
            TaskTable.NAME,
            TaskTable.Cols.UUID,
            TaskTable.Cols.TITLE,
            TaskTable.Cols.DATE,
            TaskTable.Cols.DEFERRED,
            TaskTable.Cols.REALIZED,
            TaskTable.Cols.PHOTOPATH);

        LOGGER.info("This is the sample date: " + task.getTaskRevealedDate().getTime());
        
        PreparedStatement stmt = null;
        try {

            stmt = mySqlClient.getPreparedStatement(insert);
            stmt.setString(1, task.getTaskId().toString());
            stmt.setString(2, task.getTaskTitle());
            stmt.setLong(3, task.getTaskRevealedDate().getTime());
            stmt.setInt(4, task.getTaskDeferred() ? 1 : 0);
            stmt.setInt(5, task.getTaskRealized() ? 1 : 0);
            stmt.setString(6, task.getPhotoFilePath());

            stmt.executeUpdate();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, null);
        }

        LOGGER.info("Task added to the database...");
    }


    /** Delete a Task
    *
    * @param task the Task that gets deleted
    */
    public void deleteTask(String uuid) {
        LOGGER.info("Deleteing Task with uuid: " + uuid);

        String delete = StringUtils.applyFormat(
            "DELETE FROM {0} WHERE task_uuid = \"{1}\"",
            TaskTable.NAME,
            uuid);

        int rows = mySqlClient.updateDatabaseStatement(delete);
        
        if (rows == 0) {
        	throw new NotFoundException("task: "+ uuid +" was not found, delete task failed.");
        }
        
        LOGGER.info("Task deleted from the database...");
    }

    /** Update a Task
    *
    * @param task the Task that gets updated
    */
    public void updateTask(Task task) {
    	String uuid = task.getTaskId().toString();
        LOGGER.info("Updating Task with uuid: " + uuid);

        int rows = 0;
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

        LOGGER.info(insert);
        
        PreparedStatement stmt = null;
        try {
            stmt = mySqlClient.getPreparedStatement(insert);
            stmt.setString(1, task.getTaskTitle());
            stmt.setInt(2, task.getTaskDeferred() ? 1 : 0);
            stmt.setInt(3, task.getTaskRealized() ? 1 : 0);
            stmt.setString(4, task.getPhotoFilePath());
            stmt.setString(5, uuid);

            rows = stmt.executeUpdate();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, null);
        }
        
        if (rows == 0) {
        	throw new NotFoundException("task: "+ uuid +" was not found, update task failed.");
        }

        LOGGER.info("Task updated in database...");
    }
}
