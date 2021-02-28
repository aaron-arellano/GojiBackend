package goji.task;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** Class representation of a Task object
 *
 *  @author Aaron
 *  @version 2020.12.02
 */
@JsonPropertyOrder({"taskID","taskTitle","taskRevealedDate","taskRealized","taskDeferred","photoFilePath"})
public class Task {

	@JsonProperty()
    private String taskID;
	@JsonProperty()
    private String taskTitle;
	@JsonProperty()
    private Date taskRevealedDate;
	@JsonProperty()
    private boolean taskRealized;
	@JsonProperty()
    private boolean taskDeferred;
	@JsonProperty()
    private String photoFilePath;


    /** Create a new Task object. Check in place for REST call
     *
     */
    public Task() {}

    /** Create a new Task object and sets the uuid of the Task. Primarily
     *  used when creating a Task that already exists in the Db.
     *
     * @param taskID the unique ID of the Task
     */
    public Task(UUID taskID) {
        this.taskID = taskID.toString();
    }

    public String getTaskID() {
        return taskID;
    }
    
    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public Date getTaskRevealedDate() {
        return taskRevealedDate;
    }

    public void setTaskRevealedDate(Date taskRevealedDate) {
        this.taskRevealedDate = taskRevealedDate;
    }

    public boolean getTaskRealized() {
        return taskRealized;
    }

    public void setTaskRealized(boolean taskRealized) {
        this.taskRealized = taskRealized;
    }

    public boolean getTaskDeferred() {
        return taskDeferred;
    }

    public void setTaskDeferred(boolean taskDeferred) {
        this.taskDeferred = taskDeferred;
    }

    public String getPhotoFilePath() {
        return photoFilePath;
    }

    public String setPhotoFilePath(String photoFilePath) {
        return this.photoFilePath = photoFilePath;
    }

    /** Adds new revealed entry to a TaskEntry
     *
     */
    public void addRevealed() {
        TaskEntry entry = new TaskEntry("Task Revealed", new Date(), TaskEntryKind.REVEALED);
        //taskEntries.add(entry);
    }

    /** Adds new comment entry to a TaskEntry
     *
     *  @param text the String to be added to the comment
     */
    public void addComment(String text) {
        TaskEntry entry = new TaskEntry(text, new Date(), TaskEntryKind.COMMENT);
        //taskEntries.add(entry);
    }

    /** Adds new realized entry to a TaskEntry
     *
     */
    public void addTaskRealized() {
        TaskEntry entry = new TaskEntry("Task Realized", new Date(), TaskEntryKind.REALIZED);
        //taskEntries.add(entry);
    }

    /** Adds new deferred entry to a TaskEntry
     *
     */
    public void addTaskDeferred() {
        TaskEntry entry = new TaskEntry("Task Deferred", new Date(), TaskEntryKind.DEFERRED);
        //taskEntries.add(entry);
    }
}
