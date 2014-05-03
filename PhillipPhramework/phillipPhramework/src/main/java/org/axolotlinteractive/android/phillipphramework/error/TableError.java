package org.axolotlinteractive.android.phillipphramework.error;

import org.axolotlinteractive.android.phillipphramework.datamanipulation.DatabaseTable;

public class TableError extends DatabaseTable{
	/**
	 * the table name that contains error information
	 */
	public static final String NAME="errors";
	/**
	 * TEXT = the location of where this error occurred
	 */
	public static final String COLUMN_LOCATION="location";
	/**
	 * INTEGER = the version of the application
	 */
	public static final String COLUMN_APPLICATION_VERSION = "application_version";
	/**
	 * INTEGER = the version of android that this happened on
	 */
	public static final String COLUMN_ANDROID_VERSION = "android_version";
	/**
	 * TEXT = the stack trace of the error this is a JSONArray
	 */
	public static final String COLUMN_STACK_TRACE = "stack_trace";
	/**
	 * TEXT = the message from the initial exception
	 */
	public static final String COLUMN_MESSAGE = "message";
	/**
	 * TEXT = the column that holds any extra bits of data that are application specific this a JSONObject
	 */
	public static final String COLUMN_EXTRAS = "extras";
	/**
	 * INTEGER = the column that stores if this error has been successfully sent or now. 1 if yes 0 if no defaults to 0
	 */
	public static final String COLUMN_SUCCESSFUL = "successful";
	/**
	 * TEXT = the class in which the error happened
	 */
	public static final String COLUMN_CLASS = "class";
	/**
	 * TEXT = the localized message
	 */
	public static final String COLUMN_LOCALIZED = "localized";
	/**
	 * TEXT = the time in which this error occurred from System.currentTimeMillis()
	 */
	public static final String COLUMN_TIME = "time";

	/**
	 * creates a new instance of this DatabaseTable
	 */
	public TableError()
	{
		super(NAME
			,new String[]
			{
				COLUMN_LOCATION,COLUMN_APPLICATION_VERSION,COLUMN_ANDROID_VERSION,
				COLUMN_STACK_TRACE,COLUMN_MESSAGE,COLUMN_EXTRAS,
				COLUMN_SUCCESSFUL,COLUMN_CLASS,COLUMN_LOCALIZED,
				COLUMN_TIME
			}
			,new String[]
			{
				"TEXT", "INTEGER", "INTEGER",
				"TEXT", "TEXT",	"TEXT",
				"INTEGER DEFAULT 0", "TEXT", "TEXT",
				"TEXT"
			}, false);
	}
}
