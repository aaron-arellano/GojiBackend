package goji.webapp;

import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import goji.common.AgentUtils;
import goji.common.GojiLogManagement;
import goji.data.SqlClient;

/** Startup class for the Goji backend web app. Initialization of the project begins here.
 * 
 * @author Aaron
 * @version 2020.11.23
 */
@WebListener
public class Startup implements ServletContextListener {
	
	private AgentUtils agentUtils;
	private static final Logger LOGGER = GojiLogManagement.createLogger(Startup.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent event) {
    	LOGGER.info("Setting up the agent environment");
		agentUtils = new AgentUtils();
		agentUtils.getAgent().createAgentTables();
		LOGGER.info("Done setting up the agent environment");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    	SqlClient client = agentUtils.getAgent().getSqlClient();
		LOGGER.info("Closing database connection as servlet is shutting down...");
		client.closeConnection();
    }
}
