package goji.data;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;

/** Utility class to house common methods for database operations
 *
 *
 *  @author Aaron
 *  @version 2020.10.27
 */
public final class DatabaseConfigLoader
{

    private DatabaseConfigLoader() {}


    /** Read in and return the db config as an object
     *
     * @return the db config json as a DbConfig object
     */
    public static DatabaseConfigWrapper createDbConfig() {
        Gson gson = new Gson();
        FileReader configFile = null;
        try
        {
            configFile = new FileReader("C:\\dbConfig.json");
        }
        catch (FileNotFoundException e)
        {
            System.out.println("DB Config file was not found");
        }
        DatabaseConfigWrapper dbConfig = gson.fromJson(configFile, DatabaseConfigWrapper.class);

        return dbConfig;
    }
}
