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
	
	private static GojiAgent agent;
	private SqlClient mySqlClient;
	
	private GojiAgent() { }
       
 
    public static GojiAgent getInstance() { 
        if (agent == null) {
            agent = new GojiAgent(); 
        }
        
        return agent; 
    } 

    /** Creates the agent client for non-test environment. Investigate scaling out
     *  to use CompletableFuture for asynchronous Task hosting for the environment
     *
     */
    public void createEnvironment() {
    	DatabaseConfigWrapper wrapper = DatabaseConfigLoader.createDbConfig();
    	String database = wrapper.getDatabaseName();
    	
    	//TODO look into multiple clients and async tasking for higher work loads
    	// ensure no other client is made
    	if (mySqlClient == null) {
    		mySqlClient = new MySqlClient(wrapper);
    		mySqlClient.setDefaultDatabaseCreateIfNotExists(database);
    		new TaskDbHelper(mySqlClient).createDbTable();
    	}   
    }


    /** Handler to get the Sql client for REST calls.
     * 
     * @return the SqlClient used by the REST methods in this project
     */
    public SqlClient getSqlClient() {
    	return mySqlClient;
    }
}
