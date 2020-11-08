package goji.data.test;

import goji.common.GojiLogManagement;
import goji.data.MySqlClient;
import goji.data.TaskDbHelper;
import java.sql.*;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import common.TestDatabaseConfigLoader;

/** Tests functionality of TaskDbHelper class
 *
 *  @author Aaron
 *  @version 2020.10.23
 */
public class TaskDbHelperTest {

    private TaskDbHelper taskDbHelper;
    private MySqlClient mySqlClient;
    private static final Logger LOGGER = GojiLogManagement.createLogger("Test");

    @SuppressWarnings("javadoc")
    @Before
    public void setUp() throws Exception {
        this.mySqlClient = new MySqlClient(TestDatabaseConfigLoader.createTestDbConfig());
        this.taskDbHelper = new TaskDbHelper(this.mySqlClient);
    }

    @SuppressWarnings("javadoc")
    @After
    public void tearDown() {
        mySqlClient.closeConnection(LOGGER);
    }

    @SuppressWarnings("javadoc")
    @Test
    public void createDbCreateTableTest() {
        taskDbHelper.createDbCreateTable();

        String query = "SELECT COUNT(table_name) FROM information_schema.tables WHERE table_schema='test'";
        ResultSet result = mySqlClient.queryDatabaseStatement(query);
        Statement stmt = null;
        String actual = "";

        try {
            if(result.next()){
                actual = result.getString("COUNT(table_name)");
            }
            stmt = result.getStatement();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, result, LOGGER);
        }

        assertEquals("2", actual);

        query = "SELECT table_name FROM information_schema.tables WHERE table_schema='test'";
        result = mySqlClient.queryDatabaseStatement(query);
        String[] tableNames = new String[2];
        int i = 0;

        try {
            while(result.next()) {
                tableNames[i] = result.getString("table_name");
                i++;
            }
            stmt = result.getStatement();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, result, LOGGER);
        }

        assertEquals("task", tableNames[0]);
        assertEquals("task_entry", tableNames[1]);

        // drop the tables after done testing
        String dropQuery = "DROP TABLE task_entry";
        mySqlClient.updateDatabaseStatement(dropQuery);
        String dropQuery2 = "DROP TABLE task";
        mySqlClient.updateDatabaseStatement(dropQuery2);
    }

}
