package goji.task;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** Class representation of a TaskEntry created by the end-user
 *
 *  @author Aaron
 *  @version 2020.12.02
 */
@JsonPropertyOrder({"taskEntryID","entryText","entryDate","taskEntryKind"})
public class TaskEntry {
	@JsonProperty()
    private String entryText;
	@JsonProperty()
    private Date entryDate;
	@JsonProperty()
    private TaskEntryKind taskEntryKind;
	@JsonProperty()
    private UUID taskEntryID;

    /** Create a new TaskEntry object from TaskEntryLab
     *
     * @param text the string in the task entry message
     * @param date the date which entry was made
     * @param kind the type of task entry
     */
    public TaskEntry(String text, Date date, TaskEntryKind kind) {
        this(UUID.randomUUID());
        setEntryText(text);
        setEntryDate(date);
        setTaskEntryKind(kind);
    }

    /** Secondary constructor supports creation of TaskEntry with UUID
     *
     * @param taskEntryID the unique id of the TaskEntry to retrieve
     */
    public TaskEntry(UUID taskEntryID) {
        this.taskEntryID = taskEntryID;
    }

    public String getEntryText() {
        return entryText;
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public UUID getTaskEntryID() {
        return taskEntryID;
    }
    
    public void setTaskEntryID(UUID uuid) {
    	this.taskEntryID = uuid;
    }

    public TaskEntryKind getTaskEntryKind() {
        return taskEntryKind;
    }

    public void setTaskEntryKind(TaskEntryKind taskEntryKind) {
        this.taskEntryKind = taskEntryKind;
    }
}
