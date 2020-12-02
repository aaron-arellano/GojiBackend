package goji.webapp.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import goji.common.AgentUtils;
import goji.data.SqlClient;
import goji.task.ITaskCursor;
import goji.task.Task;
import goji.task.TaskCursor;

@Path(ApiConstants.TASK_CONTROLLER_PATH)
public class TaskController {
	
	@GET
	@Path(ApiConstants.TASK_PATH)
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTask(@PathParam("taskId") String taskId) {
        
		AgentUtils agentUtils = new AgentUtils();
		
		SqlClient client = agentUtils.getAgent().getSqlClient();
		ITaskCursor cursor = new TaskCursor(client);
		
		Task task = cursor.getTask(taskId);
		
		//String taskJson = "";
		
		return Response.ok(task, MediaType.APPLICATION_JSON).build();
    }
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path(ApiConstants.TASK_PATH)
    public Response deleteTask(@PathParam("taskId") String taskId) {
        
		AgentUtils agentUtils = new AgentUtils();
		
		SqlClient client = agentUtils.getAgent().getSqlClient();
		ITaskCursor cursor = new TaskCursor(client);
		
		cursor.deleteTask(taskId);
		
        return Response.ok().build();
    }
}
