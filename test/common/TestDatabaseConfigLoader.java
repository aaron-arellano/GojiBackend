package common;
import com.google.gson.Gson;
import goji.data.DatabaseConfigWrapper;
import java.io.FileNotFoundException;
import java.io.FileReader;

/** Utility class that gets and creates the Test configuration
 *  needed for a test environment
 *
 *  @author Aaron
 *  @version 2020.10.27
 */
public final class TestDatabaseConfigLoader {

    private TestDatabaseConfigLoader() {}


    /** Read in and return the test db config as an object
     *
     * @return the test db config json as a DbConfig object
     */
    public static DatabaseConfigWrapper createTestDbConfig() {
        Gson gson = new Gson();
        FileReader configFile = null;
        try
        {
            configFile = new FileReader("C:\\dbTestConfig.json");
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Test DB Config file was not found");
        }
        DatabaseConfigWrapper dbConfig = gson.fromJson(configFile, DatabaseConfigWrapper.class);

        return dbConfig;
    }
}
