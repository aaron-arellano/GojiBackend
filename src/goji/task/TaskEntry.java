package goji.task;

import java.util.*;

/** Class representation of a TaskEntry created by the end-user
 *
 *  @author Aaron
 *  @version 2020.10.29
 */
public class TaskEntry {
    private String entryText;
    private Date entryDate;
    private TaskEntryKind taskEntryKind;
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

    @SuppressWarnings("javadoc")
    public String getEntryText() {
        return entryText;
    }

    @SuppressWarnings("javadoc")
    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    @SuppressWarnings("javadoc")
    public Date getEntryDate() {
        return entryDate;
    }

    @SuppressWarnings("javadoc")
    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    @SuppressWarnings("javadoc")
    public UUID getTaskEntryID() {
        return taskEntryID;
    }

    @SuppressWarnings("javadoc")
    public TaskEntryKind getTaskEntryKind() {
        return taskEntryKind;
    }

    @SuppressWarnings("javadoc")
    public void setTaskEntryKind(TaskEntryKind taskEntryKind)
    {
        this.taskEntryKind = taskEntryKind;
    }
}
