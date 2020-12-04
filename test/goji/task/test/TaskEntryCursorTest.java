package goji.task.test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import common.SetupTestEnv;
import common.TestDatabaseConfigLoader;
import goji.common.GojiLogManagement;
import goji.common.utils.StringUtils;
import goji.data.DatabaseConfigWrapper;
import goji.data.SqlClient;
import goji.data.TaskDbHelper;
import goji.data.TaskDbSchema.TaskTable;
import goji.task.Task;
import goji.task.TaskEntry;
import goji.task.TaskEntryCursor;
import goji.task.TaskEntryKind;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/** Test class to ensure non-regression of TaskEntryCursor DAO methods
*
*  @author Aaron
*  @version 2020.11.17
*/
public class TaskEntryCursorTest {

    private static final Logger LOGGER = GojiLogManagement.createLogger("Test");
    private SqlClient mySqlClient;
    private TaskEntryCursor taskEntryCursor;
    private TaskDbHelper taskDbHelper;
    private DatabaseConfigWrapper wrapper;

    @Before
    public void setUp() throws Exception {
        this.wrapper = TestDatabaseConfigLoader.createTestDbConfig();
        this.mySqlClient = SetupTestEnv.createTestClient(wrapper);
        this.taskEntryCursor = new TaskEntryCursor(mySqlClient);

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
    public void addTaskEntryTest() {

        Task task = addTaskToDb();

        TaskEntry taskEntry = createTaskEntry();
        String entryId = taskEntry.getTaskEntryID().toString();

        taskEntryCursor.addTaskEntry(taskEntry, task);

        String query = "SELECT COUNT(*) FROM task_entry";

        ResultSet result = mySqlClient.queryDatabaseStatement(query);
        Statement stmt = null;

        try {
            int count = 0;
            if (result.next()) {
                count = result.getInt("COUNT(*)");
            }
            assertEquals(1, count);
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }

        query = "SELECT * FROM task_entry WHERE entry_uuid = \"" + entryId+"\"";
        result = mySqlClient.queryDatabaseStatement(query);

        try {
            if(result.next()){
                String textActual = result.getString("entry_text");
                assertEquals(taskEntry.getEntryText() , textActual);

                String entryIdActual = result.getString("entry_uuid");
                assertEquals(entryId, entryIdActual);

                Date entryDateActual = new Date(result.getLong("entry_date"));
                assertEquals(0, taskEntry.getEntryDate().compareTo(entryDateActual));

                String entryKindActual = result.getString("entry_kind");
                assertEquals(TaskEntryKind.COMMENT.name(), entryKindActual);

                stmt = result.getStatement();
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
    public void updateTaskEntryTextTest() {

        Task task = addTaskToDb();

        TaskEntry taskEntry = createTaskEntry();
        String entryId = taskEntry.getTaskEntryID().toString();

        taskEntryCursor.addTaskEntry(taskEntry, task);

        String query = "SELECT entry_text FROM task_entry WHERE entry_uuid = \"" + entryId+"\"";

        ResultSet result = mySqlClient.queryDatabaseStatement(query);
        Statement stmt = null;

        try {
            String actual = "";
            if (result.next()) {
                actual = result.getString("entry_text");
            }
            assertEquals("test entry text", actual);
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }

        // the actual method to test
        String newText = "New text to set";
        taskEntry.setEntryText(newText);
        taskEntryCursor.updateTaskEntryText(taskEntry);

        result = mySqlClient.queryDatabaseStatement(query);

        try {
            String actual = "";
            if (result.next()) {
                actual = result.getString("entry_text");
            }
            assertEquals(newText, actual);
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, result);
        }
    }

    @Test
    public void getTaskEntriesTest() {

        Task task = addTaskToDb();

        TaskEntry taskEntry = createTaskEntry();
        TaskEntry taskEntry2 = createTaskEntry();
        taskEntry2.setEntryText("Text for entry2 in this task");

        taskEntryCursor.addTaskEntry(taskEntry, task);
        taskEntryCursor.addTaskEntry(taskEntry2, task);

        List<TaskEntry> entryList = taskEntryCursor.getTaskEntries(task);

        assertEquals(2, entryList.size());

        TaskEntry actualEntry = entryList.get(0);
        TaskEntry actualEntry2 = entryList.get(1);

        assertEquals(taskEntry.getEntryText(), actualEntry.getEntryText());
        assertEquals(taskEntry.getTaskEntryID().toString(), actualEntry.getTaskEntryID().toString());
        assertEquals(taskEntry.getTaskEntryKind().name(), actualEntry.getTaskEntryKind().name());
        assertEquals(0, taskEntry.getEntryDate().compareTo(actualEntry.getEntryDate()));

        assertEquals(taskEntry2.getEntryText(), actualEntry2.getEntryText());
        assertEquals(taskEntry2.getTaskEntryID().toString(), actualEntry2.getTaskEntryID().toString());
        assertEquals(taskEntry2.getTaskEntryKind().name(), actualEntry2.getTaskEntryKind().name());
        assertEquals(0, taskEntry2.getEntryDate().compareTo(actualEntry2.getEntryDate()));
    }

    private Task addTaskToDb() {
        Task task = new Task();
        task.setTaskTitle("example");
        task.setTaskRevealedDate(new Date());
        task.setTaskDeferred(true);
        task.setTaskRealized(false);
        //task.setTaskEntries(null);
        task.setPhotoFilePath("C:\\temp\\photo.jpg");

        String insert = StringUtils.applyFormat(
            "INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}) " + "VALUES(?,?,?,?,?,?)",
            TaskTable.NAME,
            TaskTable.Cols.UUID,
            TaskTable.Cols.TITLE,
            TaskTable.Cols.DATE,
            TaskTable.Cols.DEFERRED,
            TaskTable.Cols.REALIZED,
            TaskTable.Cols.PHOTOPATH);

        PreparedStatement stmt = null;
        try {

            stmt = mySqlClient.getPreparedStatement(insert);
            stmt.setString(1, task.getTaskID().toString());
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

        return task;
    }

    private TaskEntry createTaskEntry() {
        String entryText = "test entry text";
        Date entryDate = new Date();
        TaskEntry taskEntry = new TaskEntry(entryText, entryDate, TaskEntryKind.COMMENT);

        return taskEntry;
    }

}
