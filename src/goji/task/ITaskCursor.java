package goji.task;

import java.util.List;

/** Interface which defines the methods needed for a TaskCursor DAO
*
*  @author Aaron
*  @version 2020.11.07
*/
public interface ITaskCursor {

    @SuppressWarnings("javadoc")
    Task getTask(String uuid);

    @SuppressWarnings("javadoc")
    List<Task> getTasks();

    @SuppressWarnings("javadoc")
    void addTask(Task task);

    @SuppressWarnings("javadoc")
    void deleteTask(Task task);

}
