package goji.data.test;

import static org.junit.Assert.*;
import common.SetupTestEnv;
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
 *  @version 2020.11.07
 */
public class MySqlClientTest {
    private static final Logger LOGGER = GojiLogManagement.createLogger("Test");
    private SqlClient mySqlClient;
    private DatabaseConfigWrapper wrapper;


    @SuppressWarnings("javadoc")
    @Before
    public void setUp() throws Exception {
        this.wrapper = TestDatabaseConfigLoader.createTestDbConfig();
        this.mySqlClient = SetupTestEnv.createTestClient(wrapper);
    }

    @SuppressWarnings("javadoc")
    @After
    public void tearDown() {
        this.mySqlClient.closeConnection();
    }

    @SuppressWarnings("javadoc")
    @Test
    public void createDeleteDbTest() {
        SqlClient createDeleteClient = new MySqlClient(wrapper);
        // "exampleDB"
        boolean created = createDeleteClient.setDefaultDatabaseCreateIfNotExists("exampleDB");

        assertTrue(created);

        createDeleteClient.deleteDb("exampleDB");

        createDeleteClient.closeConnection();
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
            mySqlClient.closeStatementResultSet(stmt, result);
        }

        assertEquals("test", actual);

        mySqlClient.updateDatabaseStatement("DROP TABLE test");
    }
}