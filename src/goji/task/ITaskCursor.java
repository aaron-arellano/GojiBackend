package goji.task;

import java.util.List;

/** Interface which defines the methods needed for a TaskCursor DAO
*
*  @author Aaron
*  @version 2020.11.07
*/
public interface ITaskCursor {

    Task getTask(String uuid);

    List<Task> getTasks();

    Task addTask(Task task);

    void deleteTask(String uuid);
    
    Task updateTask(Task task);

}
