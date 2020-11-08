package goji.data.test;

import static org.junit.Assert.*;
import common.TestDatabaseConfigLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import goji.common.GojiLogManagement;
import goji.data.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/** Test class to ensure non-regression of data access methods
 *
 *  @author Aaron
 *  @version 2020.10.26
 */
public class MySqlClientTest {
    private MySqlClient mySqlClient;
    private DatabaseConfigWrapper wrapper;
    private static final Logger LOGGER = GojiLogManagement.createLogger("Test");

    @SuppressWarnings("javadoc")
    @Before
    public void setUp() throws Exception {
        wrapper = TestDatabaseConfigLoader.createTestDbConfig();
        this.mySqlClient = new MySqlClient(wrapper);
    }

    @SuppressWarnings("javadoc")
    @After
    public void tearDown() {
        this.mySqlClient.closeConnection(LOGGER);
    }

    @SuppressWarnings("javadoc")
    @Test
    public void createDeleteDbTest() {
        wrapper.setDatabaseName("createDelete");
        MySqlClient createDeleteClient = new MySqlClient(wrapper);
        // "exampleDB"
        boolean created = createDeleteClient.createDB();
        assertTrue(created);

        boolean deleted = createDeleteClient.deleteDB();
        assertTrue(deleted);

        createDeleteClient.closeConnection(LOGGER);
    }


    @SuppressWarnings("javadoc")
    @Test
    public void updateDatabaseStatementTest() {
        // "test"
        mySqlClient.updateDatabaseStatement("CREATE TABLE IF NOT EXISTS test (id INT)");

        ResultSet result = mySqlClient.queryDatabaseStatement("SHOW TABLES");
        Statement stmt = null;
        String actual = "";

        try {
            if(result.next()){
                actual = result.getString("Tables_in_test");
             }
            stmt = result.getStatement();
        }
        catch (SQLException se) {
            LOGGER.warning(se.toString());
        }
        finally {
            mySqlClient.closeStatementResultSet(stmt, result, LOGGER);
        }

        assertEquals("test", actual);

        mySqlClient.updateDatabaseStatement("DROP TABLE test");
    }
}