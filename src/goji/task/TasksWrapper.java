package goji.task;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/** JSON wrapper for a list of Tasks
 * 
 * @author Aaron
 * @version 2020.12.03
 */
public class TasksWrapper {

	@JsonProperty("Tasks")
	private List<Task> tasks;

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}	
}
