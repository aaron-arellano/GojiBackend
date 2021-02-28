package goji.task.test;

import static org.junit.Assert.*;
import common.SetupTestEnv;
import common.TestDatabaseConfigLoader;
import goji.common.GojiLogManagement;
import goji.data.DatabaseConfigWrapper;
import goji.data.SqlClient;
import goji.data.TaskDbHelper;
import goji.task.Task;
import goji.task.TaskCursor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Test class to ensure non-regression of TaskCursor DAO methods
*
*  @author Aaron
*  @version 2020.11.17
*/
public class TaskCursorTest {

    private static final Logger LOGGER = GojiLogManagement.createLogger("Test");
    private SqlClient mySqlClient;
    private TaskCursor taskCursor;
    private TaskDbHelper taskDbHelper;
    private DatabaseConfigWrapper wrapper;

    @Before
    public void setUp() throws Exception {
        this.wrapper = TestDatabaseConfigLoader.createTestDbConfig();
        this.mySqlClient = SetupTestEnv.createTestClient(wrapper);
        this.taskCursor = new TaskCursor(mySqlClient);

        // create the tables needed for the test
        this.taskDbHelper = new TaskDbHelper(this.mySqlClient);
        this.taskDbHelper.createDbTable();
    }

    @After
    public void tearDown() throws Exception {
        // drop the tables after done testing
        String dropQuery = "DROP TABLE task_entry";
        this.mySqlClient.updateDatabaseStatement(dropQuery);
        String dropQuery2 = "DROP TABLE task";
        this.mySqlClient.updateDatabaseStatement(dropQuery2);

        this.mySqlClient.closeConnection();
    }

    @Test
    public void getTaskTest() {
        Task task = createTask();

        taskCursor.addTask(task);

        Task actualTask = taskCursor.getTask(task.getTaskID());

        assertEquals(task.getTaskID(), actualTask.getTaskID());
        assertEquals(task.getTaskTitle(), actualTask.getTaskTitle());
        assertEquals(0, task.getTaskRevealedDate().compareTo(actualTask.getTaskRevealedDate()));
        assertEquals(task.getPhotoFilePath(), actualTask.getPhotoFilePath());
        assertTrue(actualTask.getTaskDeferred());
        assertFalse(actualTask.getTaskRealized());
    }

    @Test
    public void getTasksTest() {
        Task task = createTask();
        Task task2 = createTask();
        task2.setTaskTitle("New title for second task");

        taskCursor.addTask(task);
        taskCursor.addTask(task2);

        List<Task> actualTasks = taskCursor.getTasks();

        Task actual1 = actualTasks.get(0);
        Task actual2 = actualTasks.get(1);

        assertEquals(task.getTaskID(), actual1.getTaskID());
        assertEquals(task.getTaskTitle(), actual1.getTaskTitle());
        assertEquals(0, task.getTaskRevealedDate().compareTo(actual1.getTaskRevealedDate()));
        assertEquals(task.getPhotoFilePath(), actual1.getPhotoFilePath());
        assertTrue(actual1.getTaskDeferred());
        assertFalse(actual1.getTaskRealized());

        assertEquals(task2.getTaskID(), actual2.getTaskID());
        assertEquals(task2.getTaskTitle(), actual2.getTaskTitle());
        assertEquals(0, task2.getTaskRevealedDate().compareTo(actual2.getTaskRevealedDate()));
        assertEquals(task2.getPhotoFilePath(), actual2.getPhotoFilePath());
        assertTrue(actual2.getTaskDeferred());
        assertFalse(actual2.getTaskRealized());
    }


    @Test
    public void addTaskTest() {
        Task task = createTask();

        String taskId = task.getTaskID();

        taskCursor.addTask(task);

        String query = "SELECT * FROM task WHERE task_uuid = \"" + taskId +"\"";

        ResultSet result = mySqlClient.queryDatabaseStatement(query);
        Statement stmt = null;

        try {

            if (result.next()) {
                assertEquals(task.getTaskTitle() , result.getString("task_title"));
                assertEquals(0, task.getTaskRevealedDate().compareTo(new Date(result.getLong("task_date"))));
                assertEquals(taskId , result.getString("task_uuid"));
                assertEquals(1, result.getInt("task_deferred"));
                assertEquals(0, result.getInt("task_realized"));
                assertEquals(task.getPhotoFilePath() , result.getString("photo_path"));
            }
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, result);
        }
    }

    @Test
    public void deleteTaskTest() {
        Task task = createTask();

        taskCursor.addTask(task);

        String query = "SELECT COUNT(*) from task";
        ResultSet result = mySqlClient.queryDatabaseStatement(query);
        Statement stmt = null;

        try {

            if (result.next()) {
                assertEquals(1 , result.getInt("COUNT(*)"));
            }
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }

        taskCursor.deleteTask(task.getTaskID());
        result = mySqlClient.queryDatabaseStatement(query);
        try {

            if (result.next()) {
                assertEquals(0 , result.getInt("COUNT(*)"));
            }
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, result);
        }
    }

    @Test
    public void updateTaskTest() {
        Task task = createTask();

        taskCursor.addTask(task);

        Task getTask = taskCursor.getTask(task.getTaskID());

        assertEquals(task.getTaskID(), getTask.getTaskID());
        assertEquals(task.getTaskTitle(), getTask.getTaskTitle());
        assertEquals(0, task.getTaskRevealedDate().compareTo(getTask.getTaskRevealedDate()));
        assertEquals(task.getPhotoFilePath(), getTask.getPhotoFilePath());
        assertTrue(getTask.getTaskDeferred());
        assertFalse(getTask.getTaskRealized());

        String updateTitle = "This title was updated for the test";
        task.setTaskTitle(updateTitle);
        task.setTaskDeferred(false);
        task.setTaskRealized(true);

        getTask = taskCursor.updateTask(task);

        assertEquals(task.getTaskID(), getTask.getTaskID());
        assertEquals(updateTitle, getTask.getTaskTitle());
        assertEquals(0, task.getTaskRevealedDate().compareTo(getTask.getTaskRevealedDate()));
        assertEquals(task.getPhotoFilePath(), getTask.getPhotoFilePath());
        assertFalse(getTask.getTaskDeferred());
        assertTrue(getTask.getTaskRealized());
    }

    private Task createTask() {
        Task task = new Task(UUID.randomUUID());
        task.setTaskTitle("Adding a Task test");
        task.setTaskRevealedDate(new Date());
        task.setTaskDeferred(true);
        task.setTaskRealized(false);
        task.setPhotoFilePath("C:\\temp\\photo.jpg");

        return task;
    }


}
