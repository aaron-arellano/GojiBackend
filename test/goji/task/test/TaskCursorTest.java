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

    @SuppressWarnings("javadoc")
    @Before
    public void setUp() throws Exception {
        this.wrapper = TestDatabaseConfigLoader.createTestDbConfig();
        this.mySqlClient = SetupTestEnv.createTestClient(wrapper);
        this.taskCursor = new TaskCursor(mySqlClient);

        // create the tables needed for the test
        this.taskDbHelper = new TaskDbHelper(this.mySqlClient);
        this.taskDbHelper.createDbTable();
    }

    @SuppressWarnings("javadoc")
    @After
    public void tearDown() throws Exception {
        // drop the tables after done testing
        String dropQuery = "DROP TABLE task_entry";
        this.mySqlClient.updateDatabaseStatement(dropQuery);
        String dropQuery2 = "DROP TABLE task";
        this.mySqlClient.updateDatabaseStatement(dropQuery2);

        this.mySqlClient.closeConnection();
    }

    @SuppressWarnings("javadoc")
    @Test
    public void getTaskTest() {
        Task task = createTask();

        taskCursor.addTask(task);

        Task actualTask = taskCursor.getTask(task.getId().toString());

        assertEquals(task.getId(), actualTask.getId());
        assertEquals(task.getTitle(), actualTask.getTitle());
        assertEquals(0, task.getRevealedDate().compareTo(actualTask.getRevealedDate()));
        assertEquals(task.getPhotoFilePath(), actualTask.getPhotoFilePath());
        assertTrue(actualTask.isDeferred());
        assertFalse(actualTask.isRealized());
    }

    @SuppressWarnings("javadoc")
    @Test
    public void getTasksTest() {
        Task task = createTask();
        Task task2 = createTask();
        task2.setTitle("New title for second task");

        taskCursor.addTask(task);
        taskCursor.addTask(task2);

        List<Task> actualTasks = taskCursor.getTasks();

        Task actual1 = actualTasks.get(0);
        Task actual2 = actualTasks.get(1);

        assertEquals(task.getId(), actual1.getId());
        assertEquals(task.getTitle(), actual1.getTitle());
        assertEquals(0, task.getRevealedDate().compareTo(actual1.getRevealedDate()));
        assertEquals(task.getPhotoFilePath(), actual1.getPhotoFilePath());
        assertTrue(actual1.isDeferred());
        assertFalse(actual1.isRealized());

        assertEquals(task2.getId(), actual2.getId());
        assertEquals(task2.getTitle(), actual2.getTitle());
        assertEquals(0, task2.getRevealedDate().compareTo(actual2.getRevealedDate()));
        assertEquals(task2.getPhotoFilePath(), actual2.getPhotoFilePath());
        assertTrue(actual2.isDeferred());
        assertFalse(actual2.isRealized());
    }


    @SuppressWarnings("javadoc")
    @Test
    public void addTaskTest() {
        Task task = createTask();

        String taskId = task.getId().toString();

        taskCursor.addTask(task);

        String query = "SELECT * FROM task WHERE task_uuid = \"" + taskId +"\"";

        ResultSet result = mySqlClient.queryDatabaseStatement(query);
        Statement stmt = null;

        try {

            if (result.next()) {
                assertEquals(task.getTitle() , result.getString("task_title"));
                assertEquals(0, task.getRevealedDate().compareTo(new Date(result.getLong("task_date"))));
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

    @SuppressWarnings("javadoc")
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

        taskCursor.deleteTask(task.getId().toString());
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

    @SuppressWarnings("javadoc")
    @Test
    public void updateTaskTest() {
        Task task = createTask();

        taskCursor.addTask(task);

        Task getTask = taskCursor.getTask(task.getId().toString());

        assertEquals(task.getId(), getTask.getId());
        assertEquals(task.getTitle(), getTask.getTitle());
        assertEquals(0, task.getRevealedDate().compareTo(getTask.getRevealedDate()));
        assertEquals(task.getPhotoFilePath(), getTask.getPhotoFilePath());
        assertTrue(getTask.isDeferred());
        assertFalse(getTask.isRealized());

        String updateTitle = "This title was updated for the test";
        task.setTitle(updateTitle);
        task.setDeferred(false);
        task.setRealized(true);

        taskCursor.updateTask(task);

        getTask = taskCursor.getTask(task.getId().toString());

        assertEquals(task.getId(), getTask.getId());
        assertEquals(updateTitle, getTask.getTitle());
        assertEquals(0, task.getRevealedDate().compareTo(getTask.getRevealedDate()));
        assertEquals(task.getPhotoFilePath(), getTask.getPhotoFilePath());
        assertFalse(getTask.isDeferred());
        assertTrue(getTask.isRealized());
    }

    private Task createTask() {
        Task task = new Task();
        task.setTitle("Adding a Task test");
        task.setRevealedDate(new Date());
        task.setDeferred(true);
        task.setRealized(false);
        task.setTaskEntries(null);
        task.setPhotoFilePath("C:\\temp\\photo.jpg");

        return task;
    }


}
