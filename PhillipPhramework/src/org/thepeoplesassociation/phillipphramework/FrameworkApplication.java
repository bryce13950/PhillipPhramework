package org.thepeoplesassociation.phillipphramework;

import java.util.ArrayList;
import java.util.List;

import org.thepeoplesassociation.phillipphramework.communication.FrameworkBluetooth;
import org.thepeoplesassociation.phillipphramework.datamanipulation.DatabaseTable;
import org.thepeoplesassociation.phillipphramework.datamanipulation.FrameworkDatabase;
import org.thepeoplesassociation.phillipphramework.datamanipulation.FrameworkPreferences;
import org.thepeoplesassociation.phillipphramework.error.ErrorReport;
import org.thepeoplesassociation.phillipphramework.error.FrameworkException;
import org.thepeoplesassociation.phillipphramework.error.FrameworkExceptionHandler;
import org.thepeoplesassociation.phillipphramework.error.TableError;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;


public abstract class FrameworkApplication extends Application {
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
	public static FrameworkApplication instance;
	private FrameworkActivity lastActivity;
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
	private FrameworkDatabase database;
	/**
	 * the current instance of this application preferences
	 */
	private FrameworkPreferences preferences;
	/**
	 * the current instance of the blue tooth helper if necessary
	 */
	FrameworkBluetooth bluetooth;
	public boolean searchingForBluetooth;
	protected ArrayList<FrameworkActivity> activities = new ArrayList<FrameworkActivity>();
/****************************************************************************************************************************
 * initialization methods
 ***************************************************************************************************************************/
	/**
	 * called when the application is first created<br>
	 * @see onPreCreate()
	 * @see onPostCreate()
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
		Thread.setDefaultUncaughtExceptionHandler(new FrameworkExceptionHandler("main",Thread.getDefaultUncaughtExceptionHandler()));
	}
	/**
	 * do stuff that here that needs to be done before onCreate
	 */
	protected void onPreCreate(){}
	/**
	 * creates the database, and preferences. Also checks if this is an update
	 */
	private void initData(){
		if(getDatabaseTables()!=null){
			database=new FrameworkDatabase(this);
			if(firstCreate)onDatabaseCreated();
		}
		if(getPreferencesName()==null){
			throw new FrameworkException("Please speicify the name of the shared preferences file");
		}
		preferences=new FrameworkPreferences(this);
		checkIfUpdate();
	}
	/**
	 * override this method to do work when the application is first created
	 */
	protected void onPostCreate(){}
	/**
	 * sets if this is the first time the database was created only called from FrameworkDatabase
	 * @param b
	 */
	public void setFirstCreate(boolean b){
		firstCreate=b;
	}
/****************************************************************************************************************************
 * activity stuff
 ***************************************************************************************************************************/
	void addActivity(FrameworkActivity activity){
		lastActivity = activity;
		activities.add(activity);
	}
	void removeActivity(FrameworkActivity activity){
		activities.remove(activity);
	}
	public FrameworkActivity instanceExists(Class<?> searchFor){
		for(FrameworkActivity activity : activities){
			if(activity.getClass().isInstance(searchFor) ){
				return activity;
			}
		}
		return null;
	}
	public boolean restoreActivity(FrameworkActivity activity){
		boolean activityRestored = activity.isFinishing();
		for(int i = activities.size() - 1; i >= 0; i--){
			if(activities.get(i) != activity)
				activities.get(i).finish();
			else
				break;
		}
		return activityRestored;
	}
	public Activity getRecentActivity(){
		return lastActivity;
	}
/****************************************************************************************************************************
 * update methods
 ***************************************************************************************************************************/
	/**
	 * checks if the version of the app is the same as the version that was last ran
	 */
	private void checkIfUpdate(){
		int appVersion=0;
		try{
			appVersion=super.getPackageManager().getPackageInfo(super.getPackageName(), 0).versionCode;
		}
		catch(NameNotFoundException e){
			handleCaughtException(e,"FrameworkApplication.checkIfUpdate");
		}
		int previousVersion = getPreviousVersion(appVersion);
		if(previousVersion != appVersion){
			onUpdate(previousVersion);
		}
		preferences.put(PREFS_VERSION, appVersion);
	}
	
	protected int getPreviousVersion(int appVersion){
		return preferences.getInt(PREFS_VERSION, appVersion);
	}
	/**
	 * called the first time the application is opened after an update
	 */
	protected void onUpdate(int previousVersion){
		for(DatabaseTable table : getDatabaseTables()){
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
	public final List<DatabaseTable> getAllDatabaseTables(){
		List<DatabaseTable> returnVals=new ArrayList<DatabaseTable>();
		returnVals.add(TableError.STRUCTURE);
		DatabaseTable[] appTables=getDatabaseTables();
		for(int i=0;i<appTables.length;i++){
			returnVals.add(appTables[i]);
		}
		logDebug(returnVals.toString());
		return returnVals;
	}
	/**
	 * @see org.thepeoplesassociation.phillipphramework.datamanipulation.DatabaseTable
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
	public void onDatabaseCreated(){}
	/**
	 * @return a handle to the database helper for this application
	 */
	public FrameworkDatabase getDatabase(){
		return database;
	}
/****************************************************************************************************************************
 * preferences methods
 ***************************************************************************************************************************/
	/**
	 * @return a handle to the shared preferences for this application
	 */
	public FrameworkPreferences getPreferences(){
		return preferences;
	}
/****************************************************************************************************************************
 * Bluetooth stuff
 ***************************************************************************************************************************/
	/**
	 * @return a handle to the bluetooth helper
	 */
	public FrameworkBluetooth getBluetooth(){
		return bluetooth;
	}
	
	@SuppressLint("NewApi")
	public void checkIfBluetoothEnabled(){
		if(!bluetooth.getAdapter().isEnabled()){
			if(forceBluetooth())
				bluetooth.getAdapter().enable();
			else{
				Activity activity = getRecentActivity();
				if(activity != null && activity instanceof FrameworkActivity){
					((FrameworkActivity)activity).requestBluetoothEnable();
				}
			}
		}
	}
	/**
	 * checks if we want to force the bluetooth on
	 * @return false by default
	 */
	public boolean forceBluetooth(){
		return false;
	}
	public void onBluetoothDeviceDiscovered(BluetoothDevice device){}
	public void doneFetchingBluetooth(ArrayList<BluetoothDevice> devices){}
/****************************************************************************************************************************
 * exception methods
 ***************************************************************************************************************************/
	/**
	 * @return the current running instance of this application
	 */
	public static FrameworkApplication getInstance(){
		return instance;
	}
	/**
	 * @return the log tag for debugging
	 */
	protected abstract String getLogTag();
	/**
	 * sends a log message to logcat
	 * @param message the message to log
	 */
	public static void logDebug(String message){
		if(instance.getLogTag()==null){
			throw new FrameworkException("You forgot to specify a log tag, you are indeed retarded");
		}
		if(instance.getDebuggable())Log.d(instance.getLogTag(),message);
	}
	
	public static void handleCaughtException(Throwable t, String name){
		handleCaughtException(t, name, "");
	}
	public abstract String getErrorURL();
	/**
	 * handles any caught exceptions for me
	 * @param t the thing that was thrown
	 * @param name the name of the location of the exception
	 */
	public static void handleCaughtException(Throwable t,String name, String extras){
		Log.d(instance.getLogTag(),name+"\n"+t.getMessage());
		new ErrorReport(t, name, extras);
	}
	public abstract boolean getDebuggable();
	
	public abstract String getApplicationName();
}