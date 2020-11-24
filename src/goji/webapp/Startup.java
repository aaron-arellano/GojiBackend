package goji.webapp;

import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;
import goji.common.AgentUtils;
import goji.common.GojiLogManagement;
import goji.data.SqlClient;

/** Startup class for the Goji backend web app. Initialization of the project begins here.
 * 
 * @author Aaron
 * @version 2020.11.23
 */
@SuppressWarnings("serial")
public class Startup extends HttpServlet {
	private AgentUtils agentUtils;
	
	private static final Logger LOGGER = GojiLogManagement.createLogger(Startup.class.getName());

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		LOGGER.info("Setting up the agent environment");
		agentUtils = new AgentUtils();
		agentUtils.getAgent().createAgentTables();
		LOGGER.info("Done setting up the agent environment");
	}
	
	@Override
	public void destroy() {
		SqlClient client = agentUtils.getAgent().getSqlClient();
		LOGGER.info("Closing database connection as servlet is shutting down...");
		client.closeConnection();
	}
}
