package org.axolotlinteractive.phillipphramework;

import java.util.ArrayList;
import java.util.List;

import org.axolotlinteractive.phillipphramework.datamanipulation.DatabaseTable;
import org.axolotlinteractive.phillipphramework.datamanipulation.PhrameworkDatabase;
import org.axolotlinteractive.phillipphramework.datamanipulation.PhrameworkPreferences;
import org.axolotlinteractive.phillipphramework.error.ErrorReport;
import org.axolotlinteractive.phillipphramework.error.PhrameworkException;
import org.axolotlinteractive.phillipphramework.error.PhrameworkExceptionHandler;
import org.axolotlinteractive.phillipphramework.error.TableError;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.util.Log;

/**
 * 
 * @author Bryce Meyer 414-208-0180 info@axolotlinteractive.com https://github.com/bryce13950
 *
 */
public abstract class PhrameworkApplication extends Application
{
/****************************************************************************************************************************
 * variables
 ***************************************************************************************************************************/
	/**
	 * the name of the preference that contains the app version
	 */
	protected static final String PREFS_VERSION="app_version";
	
	public static final int REQUEST_PLAYSERVICE_ERROR = 2572;
	/**
	 * a static reference to the current running application
	 */
	public static PhrameworkApplication instance;
	private PhrameworkActivity lastActivity;
	/**
	 * boolean value for if this is the first time the database has been created.<br>
	 * this is important because if the database needs to have some values inserted
	 * right away then we need to set this to true during database creation.<br>
	 * so when the database has been returned we can insert into it
	 */
	protected boolean firstCreate=false;
	/**
	 * the current instance of this applications database
	 */
	private PhrameworkDatabase database;
	/**
	 * the current instance of this application preferences
	 */
	private PhrameworkPreferences preferences;
	/**
	 * The lastLocation that was received
	 */
	public static Location lastLocation;
	/**
	 * A list of activities currently running in order of when they were opened
	 */
	protected ArrayList<PhrameworkActivity> activities = new ArrayList<PhrameworkActivity>();
/****************************************************************************************************************************
 * initialization methods
 ***************************************************************************************************************************/
	/**
	 * called when the application is first created
	 */
	@Override
	public final void onCreate(){
		initExceptionHandling();
		onPreCreate();
		initData();
		onPostCreate();
	}
	/**
	 * initializes exception handling for the main thread of the app
	 */
	private void initExceptionHandling(){
		instance=this;
		Thread.setDefaultUncaughtExceptionHandler(new PhrameworkExceptionHandler("main",Thread.getDefaultUncaughtExceptionHandler()));
	}
	/**
	 * do stuff that here that needs to be done before onCreate
	 */
	protected void onPreCreate(){}
	/**
	 * creates the database, and preferences. Also checks if this is an update
	 */
	private void initData()
	{
		if(getDatabaseTables()!=null)
		{
			database = new PhrameworkDatabase(this);
			if(firstCreate)
				onDatabaseCreated();
		}
		if(getPreferencesName() == null)
		{
			throw new PhrameworkException("Please specify the name of the shared preferences file");
		}
		preferences = new PhrameworkPreferences(this);
		checkIfUpdate();
	}
	/**
	 * override this method to do work when the application is first created
	 */
	protected void onPostCreate()
	{
	}
	/**
	 * sets if this is the first time the database was created only called from FrameworkDatabase
	 * @param b
	 */
	public void setFirstCreate(boolean b)
	{
		firstCreate=b;
	}
/****************************************************************************************************************************
 * activity stuff
 ***************************************************************************************************************************/
	/**
	 * adds a new activity to the activities history
	 * @param activity the new PhrameworkActivity
	 */
	void addActivity(PhrameworkActivity activity)
	{
		lastActivity = activity;
		activities.add(activity);
	}

	/**
	 * remove a activity from the history
	 * @param activity the PhrameworkActivity to remove
	 */
	void removeActivity(PhrameworkActivity activity)
	{
		activities.remove(activity);
	}

	/**
	 * looks to see if there is a current running instance of a activity which extends PhrameworkActivity
	 * @param searchFor the class that we are searching for
	 * @return the current running instance of the activity
	 */
	public PhrameworkActivity instanceExists(Class<?> searchFor)
	{
		for(PhrameworkActivity activity : activities)
		{
			if(activity.getClass().isInstance(searchFor) )
			{
				return activity;
			}
		}
		return null;
	}
	public boolean restoreActivity(PhrameworkActivity activity)
	{
		boolean activityRestored = activity.isFinishing();
		for(int i = activities.size() - 1; i >= 0; i--)
		{
			if(activities.get(i) != activity)
				activities.get(i).finish();
			else
				break;
		}
		return activityRestored;
	}
	public Activity getRecentActivity()
	{
		return lastActivity;
	}
/****************************************************************************************************************************
 * update methods
 ***************************************************************************************************************************/
	/**
	 * checks if the version of the app is the same as the version that was last ran
	 */
	private void checkIfUpdate()
	{
		int appVersion=0;
		try
		{
			appVersion=super.getPackageManager().getPackageInfo(super.getPackageName(), 0).versionCode;
		}
		catch(NameNotFoundException e)
		{
			handleCaughtException(e,"FrameworkApplication.checkIfUpdate");
		}
		int previousVersion = getPreviousVersion(appVersion);
		if(previousVersion != appVersion)
		{
			onUpdate(previousVersion);
		}
		preferences.put(PREFS_VERSION, appVersion);
	}
	
	protected int getPreviousVersion(int appVersion)
	{
		return preferences.getInt(PREFS_VERSION, appVersion);
	}
	/**
	 * called the first time the application is opened after an update
	 */
	protected void onUpdate(int previousVersion)
	{
		for(DatabaseTable table : getDatabaseTables())
		{
			table.applicationUpdated(previousVersion);
		}
	}
/****************************************************************************************************************************
 * database methods
 ***************************************************************************************************************************/
	/**
	 * @return the name of the database for this application<br><br>null if there is no need for a database
	 */
	public abstract String getDatabaseName();
	/**
	 * @return the version of the database for this application<br><br>-1 if there is no need for a database
	 */
	public abstract int getDatabaseVersion();
	/**
	 * get the tables that are needed for this entire application
	 * @return a list of all of the database tables
	 */
	public final List<DatabaseTable> getAllDatabaseTables()
	{
		List<DatabaseTable> returnVals=new ArrayList<DatabaseTable>();
		returnVals.add(new TableError());
		DatabaseTable[] appTables=getDatabaseTables();
		for(int i=0;i<appTables.length;i++)
		{
			returnVals.add(appTables[i]);
		}
		logDebug(returnVals.toString());
		return returnVals;
	}
	/**
	 * @see org.axolotlinteractive.phillipphramework.datamanipulation.DatabaseTable
	 * @return the structure of the database for this application<br><br>null if there is no need for a database
	 */
	protected abstract DatabaseTable[] getDatabaseTables();
	/**
	 * @return the name of the shared preferences file<br><br>null if there is no need for shared preferences
	 */
	public abstract String getPreferencesName();
	/**
	 * override this method to immediately insert values into the database after its first creation
	 */
	public void onDatabaseCreated()
	{
	}
	/**
	 * @return a handle to the database helper for this application
	 */
	public PhrameworkDatabase getDatabase()
	{
		return database;
	}
/****************************************************************************************************************************
 * preferences methods
 ***************************************************************************************************************************/
	/**
	 * @return a handle to the shared preferences for this application
	 */
	public PhrameworkPreferences getPreferences()
	{
		return preferences;
	}
/****************************************************************************************************************************
 * exception methods
 ***************************************************************************************************************************/
	/**
	 * @return the current running instance of this application
	 */
	public static PhrameworkApplication getInstance()
	{
		return instance;
	}
	/**
	 * sends a log message to logcat
	 * @param message the message to log
	 */
	public static void logDebug(String message)
	{
		if(instance.getApplicationName()==null)
		{
			throw new PhrameworkException("You forgot to specify a log tag, we need that in order to log messages");
		}
		if(instance.getDebuggable())Log.d(instance.getApplicationName(),message);
	}
	
	public static void handleCaughtException(Throwable t, String name)
	{
		handleCaughtException(t, name, "");
	}
	public abstract String getErrorURL();
	/**
	 * handles any caught exceptions for me
	 * @param t the thing that was thrown
	 * @param name the name of the location of the exception
	 */
	public static void handleCaughtException(Throwable t,String name, String extras)
	{
		Log.d(instance.getApplicationName(),name+"\n"+t.getMessage());
		new ErrorReport(t, name, extras);
	}
	public abstract boolean getDebuggable();
	
	public abstract String getApplicationName();
}