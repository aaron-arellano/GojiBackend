package goji.webapp.api;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import goji.common.AgentUtils;
import goji.common.GojiLogManagement;
import goji.common.JsonObjectMapper;
import goji.common.utils.ObjectUtils;
import goji.data.SqlClient;
import goji.task.ITaskCursor;
import goji.task.Task;
import goji.task.TaskCursor;

@Path(ApiConstants.TASK_CONTROLLER_PATH)
public class TaskController {
	private static final Logger LOGGER = GojiLogManagement.createLogger(TaskController.class.getName());
	
	@GET
	@Path(ApiConstants.TASK_PATH)
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTask(@PathParam("taskId") String taskId) { 
		validUuid(taskId);
		
		AgentUtils agentUtils = new AgentUtils();
		SqlClient client = agentUtils.getAgent().getSqlClient();
		ITaskCursor cursor = new TaskCursor(client);
		
		LOGGER.info("Start GET task for user: " + taskId);
		Task task = cursor.getTask(taskId);
		String result = JsonObjectMapper.objectToJsonString(task);
		LOGGER.info("Done returning GET task for user: " + result);
		
		return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }
	
	@DELETE
	@Path(ApiConstants.TASK_PATH)
	@Produces(MediaType.APPLICATION_JSON)
    public Response deleteTask(@PathParam("taskId") String taskId) {
		validUuid(taskId);
		
		AgentUtils agentUtils = new AgentUtils();
		SqlClient client = agentUtils.getAgent().getSqlClient();
		ITaskCursor cursor = new TaskCursor(client);
		
		LOGGER.info("Start delete task for user: " + taskId);
		cursor.deleteTask(taskId);
		LOGGER.info("Stop delete task for user: " + taskId);
		
        return Response.ok().build();
    }
	
	@PUT
	@Path(ApiConstants.TASK_PATH)
    @Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
    public Response updateTask(@PathParam("taskId") String taskId, Task task) {
		validUuid(taskId);
		ObjectUtils.validateObjectNotNull(task);
		
		AgentUtils agentUtils = new AgentUtils();
		SqlClient client = agentUtils.getAgent().getSqlClient();
		ITaskCursor cursor = new TaskCursor(client);
		Task taskWrapper = wrapTask(taskId, task);
		
		LOGGER.info("Start update task for user: " + taskId);
		Task updatedTask = cursor.updateTask(taskWrapper);
		String result = JsonObjectMapper.objectToJsonString(updatedTask);
		LOGGER.info("Stop update task for user: " + taskId);
		
		return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }
	
	@PUT
    @Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
    public Response createTask(Task task) {
		ObjectUtils.validateObjectNotNull(task);
		
		AgentUtils agentUtils = new AgentUtils();
		SqlClient client = agentUtils.getAgent().getSqlClient();
		ITaskCursor cursor = new TaskCursor(client);
		String uuid = task.getTaskID().toString();
		task.setTaskRevealedDate(new Date());
		
		LOGGER.info("Start add task for user: " + uuid);
		Task addedTask = cursor.addTask(task);
		String result = JsonObjectMapper.objectToJsonString(addedTask);
		LOGGER.info("Stop add task for user: " + uuid);
		
		return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }
	
	private void validUuid(String uuid) {
		String regex = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
		
		if(!uuid.matches(regex)) {
			LOGGER.severe("User task id not valid " + uuid);
			throw new BadRequestException("Provided task id is not valid " + uuid);
		}
	}
	
	private Task wrapTask(String uuid, Task task) {
		Task ans = new Task(UUID.fromString(uuid));
		ans.setTaskTitle(task.getTaskTitle());
		ans.setTaskRealized(task.getTaskRealized());
		ans.setTaskDeferred(task.getTaskDeferred());
		ans.setPhotoFilePath(task.getPhotoFilePath());
		
		return ans;
	}
}
