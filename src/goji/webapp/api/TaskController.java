package goji.webapp.api;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;
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
import goji.task.TasksWrapper;

@Path(ApiConstants.TASK_CONTROLLER_PATH)
public class TaskController {
	private static final Logger LOGGER = GojiLogManagement.createLogger(TaskController.class.getName());
	
	@GET
	@Path(ApiConstants.TASK_PATH)
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTask(@PathParam("taskId") String taskId) { 
		ObjectUtils.validUuid(taskId);
		
		ITaskCursor cursor = getTaskCursor();
		
		LOGGER.info("Start GET task for user: " + taskId);
		Task task = cursor.getTask(taskId);
		String result = JsonObjectMapper.objectToJsonString(task);
		LOGGER.info("Done returning GET task for user: " + result);
		
		return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }
	
	@GET
	@Path(ApiConstants.TASK_LIST_PATH)
	@Produces({MediaType.APPLICATION_JSON})
	public Response getTasks() {
		ITaskCursor cursor = getTaskCursor();
		TasksWrapper wrapper = new TasksWrapper();
		
		LOGGER.info("Start GET all tasks for user");
		wrapper.setTasks(cursor.getTasks());
		String result = JsonObjectMapper.objectToJsonString(wrapper);
		LOGGER.info("Stop GET all tasks for user");
		
		return Response.ok(result, MediaType.APPLICATION_JSON).build();
	}
	
	@PUT
    @Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
    public Response addUpdateTask(Task task) {
		ObjectUtils.validateObjectNotNull(task);
		ITaskCursor cursor = getTaskCursor();
		String taskId = "";
		String result = "";

		if (task.getTaskID() != null) {
			taskId = task.getTaskID();
			ObjectUtils.validUuid(taskId);
			LOGGER.info("Start update task for user: " + taskId);
			Task updatedTask = cursor.updateTask(task);
			result = JsonObjectMapper.objectToJsonString(updatedTask);
			LOGGER.info("Stop update task for user: " + taskId);
		}
		else {
			taskId = UUID.randomUUID().toString();
			task.setTaskID(taskId);
			task.setTaskRevealedDate(new Date());
		
			LOGGER.info("Start add task for user: " + taskId);
			Task addedTask = cursor.addTask(task);
			result = JsonObjectMapper.objectToJsonString(addedTask);
			LOGGER.info("Stop add task for user: " + taskId);
		}
		
		return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }
	
	@DELETE
	@Path(ApiConstants.TASK_PATH)
	@Produces(MediaType.APPLICATION_JSON)
    public Response deleteTask(@PathParam("taskId") String taskId) {
		ObjectUtils.validUuid(taskId);
		
		ITaskCursor cursor = getTaskCursor();
		
		LOGGER.info("Start delete task for user: " + taskId);
		cursor.deleteTask(taskId);
		LOGGER.info("Stop delete task for user: " + taskId);
		
        return Response.ok().build();
    }
		
	private ITaskCursor getTaskCursor() {
		AgentUtils agentUtils = new AgentUtils();
		SqlClient client = agentUtils.getAgent().getSqlClient();
		ITaskCursor cursor = new TaskCursor(client);
		
		return cursor;
	}

}
