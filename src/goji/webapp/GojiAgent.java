package goji.webapp;

import goji.data.DatabaseConfigLoader;
import goji.data.DatabaseConfigWrapper;
import goji.data.MySqlClient;
import goji.data.SqlClient;
import goji.data.TaskDbHelper;

/** Implemented as a singleton as the environment object should be instantiated only once.
 *
 *  @author Aaron
 *  @version 2020.11.09
 */
public class GojiAgent {

	private SqlClient mySqlClient;
	private String database;
 
	/** Constructor supports multiple creations of the agent and underlying
	 *  SQL client for further scaling of the service.
	 */
    public GojiAgent () { 
    	DatabaseConfigWrapper wrapper = DatabaseConfigLoader.createDbConfig();
    	database = wrapper.getDatabaseName();
    	
    	mySqlClient = new MySqlClient(wrapper); 
    } 

    
    /** Handler to get the Sql client for REST calls.
     * 
     * @return the SqlClient used by the REST methods in this project
     */
    public SqlClient getSqlClient() {
    	return mySqlClient;
    }
    
    /** Method should only be called during startup of the servlet to 
     *  ensure database is ready.
     * 
     */
    public void createAgentTables() {
    	mySqlClient.setDefaultDatabaseCreateIfNotExists(database);
    	new TaskDbHelper(mySqlClient).createDbTable();  
    }
}
