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
import java.util.logging.Logger;

/** Test class to ensure non-regression of TaskEntryCursor DAO methods
*
*  @author Aaron
*  @version 2020.11.16
*/
public class TaskEntryCursorTest {

    private static final Logger LOGGER = GojiLogManagement.createLogger("Test");
    private SqlClient mySqlClient;
    private TaskEntryCursor taskEntryCursor;
    private TaskDbHelper taskDbHelper;
    private DatabaseConfigWrapper wrapper;

    @SuppressWarnings("javadoc")
    @Before
    public void setUp() throws Exception {
        this.wrapper = TestDatabaseConfigLoader.createTestDbConfig();
        this.mySqlClient = SetupTestEnv.createTestClient(wrapper);
        this.taskEntryCursor = new TaskEntryCursor(mySqlClient);

        // create the tables needed for the test
        this.taskDbHelper = new TaskDbHelper(this.mySqlClient);
        this.taskDbHelper.createDbCreateTable();
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
    public void addTaskEntryTest() {
        Task task = new Task();
        task.setTitle("example");
        task.setCheckboxDate(new Date());
        task.setRevealedDate(new Date());
        task.setDeferred(true);
        task.setRealized(false);
        task.setTaskEntries(null);
        task.setPhotoFilePath("C:\\temp\\photo.jpg");

        // add task to database for FK constraint
        addTaskToDb(task);

        String entryText = "test entry text";
        Date entryDate = new Date();
        TaskEntry taskEntry = new TaskEntry(entryText, entryDate, TaskEntryKind.COMMENT);
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
            // do nothing
        }

        query = "SELECT * FROM task_entry WHERE entry_uuid = \"" + entryId+"\"";
        result = mySqlClient.queryDatabaseStatement(query);

        try {
            String textActual = result.getString("entry_text");
            assertEquals(entryText, textActual);

            String entryIdActual = result.getString("entry_uuid");
            assertEquals(entryId, entryIdActual);

            Date entryDateActual = new Date(result.getLong("entry_date"));
            assertEquals(0, entryDate.compareTo(entryDateActual));

            String entryKindActual = result.getString("entry_kind");
            assertEquals(TaskEntryKind.COMMENT.name(), entryKindActual);

            stmt = result.getStatement();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, result);
        }
    }


    private void addTaskToDb(Task task) {
        String insert = StringUtils.applyFormat(
            "INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}) " + "VALUES(?,?,?,?,?,?)",
            TaskTable.NAME,
            TaskTable.Cols.UUID,
            TaskTable.Cols.TITLE,
            TaskTable.Cols.DATE,
            TaskTable.Cols.DEFERRED,
            TaskTable.Cols.REALIZED,
            TaskTable.Cols.PHOTOPATH);

        // add the Task
        PreparedStatement stmt = null;
        try {

            stmt = mySqlClient.getPreparedStatement(insert);
            stmt.setString(1, task.getId().toString());
            stmt.setString(2, task.getTitle());
            stmt.setLong(3, task.getRevealedDate().getTime());
            stmt.setInt(4, task.isDeferred() ? 1 : 0);
            stmt.setInt(5, task.isRealized() ? 1 : 0);
            stmt.setString(6, task.getPhotoFilePath());

            stmt.executeUpdate();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, null);
        }
    }

}
