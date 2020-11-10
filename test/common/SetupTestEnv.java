package common;

import goji.data.DatabaseConfigWrapper;
import goji.data.MySqlClient;
import goji.data.SqlClient;

/** Utility class whose sole purpose is to set up testing environment.
 *
 *  @author Aaron
 *  @version 2020.11.09
 */
public class SetupTestEnv {

    /** Creates test client for particular test instance
     *
     * @param wrapper the configuration needed for the client
     * @return the test client
     */
    public static SqlClient createTestClient(DatabaseConfigWrapper wrapper) {
        SqlClient testClient = new MySqlClient(wrapper);
        String database = wrapper.getDatabaseName();
        testClient.setDefaultDatabaseCreateIfNotExists(database);

        return testClient;
    }

}
