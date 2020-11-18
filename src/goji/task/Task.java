package goji.task;

import java.util.*;

/** Class representation of a Task object
 *
 *  @author Aaron
 *  @version 2020.10.30
 */
public class Task {

    private UUID taskID;
    private String taskTitle;
    private Date taskRevealedDate;
    private boolean taskRealized;
    private boolean taskDeferred;
    private String photoFilePath;
    private List<TaskEntry> taskEntries;

    /** Create a new Task object.
     *
     */
    public Task() {
        this(UUID.randomUUID());
        taskTitle = null;
        taskRealized = false;
        taskDeferred = false;
        taskEntries = new ArrayList<>();
    }

    /** Create a new Task object and sets the uuid of the Task
     *
     * @param taskID the unique ID of the Task
     */
    public Task(UUID taskID) {
        this.taskID = taskID;
        taskRevealedDate = new Date();
    }

    @SuppressWarnings("javadoc")
    public UUID getId() {
        return taskID;
    }

    @SuppressWarnings("javadoc")
    public String getTitle() {
        return taskTitle;
    }

    @SuppressWarnings("javadoc")
    public void setTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    @SuppressWarnings("javadoc")
    public List<TaskEntry> getTaskEntries() {
        return taskEntries;
    }

    @SuppressWarnings("javadoc")
    public void setTaskEntries(List<TaskEntry> taskEntries) {
        this.taskEntries = taskEntries;
    }

    @SuppressWarnings("javadoc")
    public Date getRevealedDate() {
        return taskRevealedDate;
    }

    @SuppressWarnings("javadoc")
    public void setRevealedDate(Date taskRevealedDate) {
        this.taskRevealedDate = taskRevealedDate;
    }


    @SuppressWarnings("javadoc")
    public boolean isRealized() {
        return taskRealized;
    }

    @SuppressWarnings("javadoc")
    public void setRealized(boolean taskRealized) {
        this.taskRealized = taskRealized;
    }

    @SuppressWarnings("javadoc")
    public boolean isDeferred() {
        return taskDeferred;
    }

    @SuppressWarnings("javadoc")
    public void setDeferred(boolean taskDeferred) {
        this.taskDeferred = taskDeferred;
    }

    @SuppressWarnings("javadoc")
    public String getPhotoFilePath() {
        return photoFilePath;
    }

    @SuppressWarnings("javadoc")
    public String setPhotoFilePath(String photoFilePath) {
        return this.photoFilePath = photoFilePath;
    }

    /** Adds new revealed entry to a TaskEntry
     *
     */
    public void addRevealed() {
        TaskEntry entry = new TaskEntry("Task Revealed", new Date(), TaskEntryKind.REVEALED);
        taskEntries.add(entry);
    }

    /** Adds new comment entry to a TaskEntry
     *
     *  @param text the String to be added to the comment
     */
    public void addComment(String text) {
        TaskEntry entry = new TaskEntry(text, new Date(), TaskEntryKind.COMMENT);
        taskEntries.add(entry);
    }

    /** Adds new realized entry to a TaskEntry
     *
     */
    public void addTaskRealized() {
        TaskEntry entry = new TaskEntry("Task Realized", new Date(), TaskEntryKind.REALIZED);
        taskEntries.add(entry);
    }

    /** Adds new deferred entry to a TaskEntry
     *
     */
    public void addTaskDeferred() {
        TaskEntry entry = new TaskEntry("Task Deferred", new Date(), TaskEntryKind.DEFERRED);
        taskEntries.add(entry);
    }

    /** Method to update task entry comments
     *
     * @param comment updated comment to add to the TaskEntry
     * @param uuid unique ID used to find the TaskEntry
     */
    public void updateEntryComment(String comment, String uuid) {
        int pos = 0;
        for (TaskEntry entry : taskEntries) {
            if (entry.getTaskEntryID().toString().equals(uuid)) {
                break;
            }
            pos++;
        }
        TaskEntry entry = taskEntries.get(pos);
        // update the database entry here

        entry.setEntryText(comment);
        taskEntries.set(pos, entry);
    }

    /** Removes realized entry from a TaskEntry
    *
    */
    public void removeTaskRealized() {
        Iterator<TaskEntry> iterator = taskEntries.iterator();
        while (iterator.hasNext()) {
            TaskEntry e = iterator.next();
            if (e.getTaskEntryKind() == TaskEntryKind.REALIZED) {
                iterator.remove();
            }
        }
    }

    /** Removes deferred entry from a TaskEntry
    *
    */
    public void removeTaskDeferred() {
        Iterator<TaskEntry> iterator = taskEntries.iterator();
        while (iterator.hasNext()) {
            TaskEntry e = iterator.next();
            if (e.getTaskEntryKind() == TaskEntryKind.DEFERRED) {
                iterator.remove();
            }
        }
    }
}
