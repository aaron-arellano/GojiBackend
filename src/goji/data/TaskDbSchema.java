package goji.data;

/** Class representations of the schemas for the two tables in the db
 *
 *
 *  @author Aaron
 *  @version 2020.10.27
 */
public class TaskDbSchema {

    @SuppressWarnings("javadoc")
    public static final class TaskTable {
        public static final String NAME = "task";

        public static final class Cols {
            public static final String UUID = "task_uuid";
            public static final String TITLE = "task_title";
            public static final String DATE = "task_date";
            public static final String DEFERRED = "task_deferred";
            public static final String REALIZED = "task_realized";
            public static final String PHOTOPATH = "photo_path";
        }
    }

    @SuppressWarnings("javadoc")
    public static final class TaskEntryTable {
        public static final String NAME = "task_entry";

        public static final class Cols {
            public static final String TEXT = "entry_text";
            public static final String DATE = "entry_date";
            public static final String KIND = "entry_kind";
            public static final String ENTRYID = "entry_uuid";
            public static final String UUID = "task_uuid";
        }
    }
}
