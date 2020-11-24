package goji.common;

import goji.webapp.GojiAgent;

/** Creates global instance of the agent to be used in this project. Intent is for
 *  only one agent per project, but multiple agents can be created such as for 
 *  testing, other project references, etc.
 * 
 * @author Aaron
 * @version 2020.11.24
 */
public class AgentUtils {
	
	private GojiAgent agent;
	// global instance of Agent for use through this project only
	// GojiAgent is extensible to other project through that class.
	private static GojiAgent agentInstance;
	
	/** Constructor ensures we get the project instance of the Agent
	 * 
	 */
	public AgentUtils() {
		if (AgentUtils.agentInstance == null) {
			AgentUtils.agentInstance = new GojiAgent();
		}
		
		agent = AgentUtils.agentInstance;
	}

	/** Get the Agent, creates if it does not exist
	 * 
	 * @return
	 */
	public GojiAgent getAgent() {		
		return agent;
	}
}
