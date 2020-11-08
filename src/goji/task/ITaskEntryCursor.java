package goji.task;

import java.util.List;

/** Interface which defines the methods needed for a TaskEntryCursor DAO
 *
 *  @author Aaron
 *  @version 2020.11.07
 */
public interface ITaskEntryCursor {

    @SuppressWarnings("javadoc")
    void addTaskEntry(TaskEntry taskEntry, Task task);

    @SuppressWarnings("javadoc")
    void updateTaskEntryText(TaskEntry taskEntry);

    @SuppressWarnings("javadoc")
    List<TaskEntry>getTaskEntries(Task task);
}
