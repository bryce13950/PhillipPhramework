package org.axolotlinteractive.phillipphramework.communication;

import java.util.HashMap;
import java.util.List;

import org.axolotlinteractive.phillipphramework.PhrameworkApplication;
import org.axolotlinteractive.phillipphramework.error.ErrorReport;
import org.axolotlinteractive.phillipphramework.error.PhrameworkExceptionHandler;
import org.axolotlinteractive.phillipphramework.error.TableError;

import android.os.AsyncTask;

/**
 * This class should be used as the base class for all async tasks
 * @param <Params> what type of object execute, and doWork take
 * @param <Progress> what type of object is posted back to the ui thread in onProgressUpdate
 * @param <Result> the result that will be returned from doWork, and will also be the parameter of handlePostExecute
 */
public abstract class PhrameworkAsyncTask<Params,Progress,Result> extends AsyncTask<Params,Progress,Result>
{

	/**
	 * This is the name of the thread. This is used to label this task in case we have any uncaught exceptions that get thrown
	 */
	protected String ThreadName;

	/**
	 * Use this constructor since we need to know the name of this thread
	 * @param name the name of the thread that this task will take on
	 */
	protected PhrameworkAsyncTask(String name)
	{
		super();
		ThreadName=name;
	}

	/**
	 * This method is made final, so we can set our exception handler which will report any uncaught exceptions to our servers
	 * @param params the object that was passed in via execute
	 * @return
	 */
	@Override
	protected final Result doInBackground(Params... params)
	{
		Thread.setDefaultUncaughtExceptionHandler(new PhrameworkExceptionHandler(ThreadName,Thread.getDefaultUncaughtExceptionHandler()));
		return doWork(params);
	}

	/**
	 * This is where you should do what you need to do in the background, this essentially replaces the build in android doInBackground
	 * @param params the params that are required to run this call
	 * @return the object that will be returned from this call
	 */
	protected abstract Result doWork(Params... params);

	/**
	 * This method is made final, so it can send in any possible cached error reports that exist in this application
	 * @param result the result that was returned from doWork
	 */
	protected final void onPostExecute(Result result)
	{
		handlePostExecute(result);
		sendCachedData();
		if(PhrameworkApplication.instance != null && PhrameworkApplication.instance.getDatabase() != null)
		{
			List<HashMap<String,String>> errors = PhrameworkApplication.instance.getDatabase().getTableWhere(TableError.NAME,TableError.COLUMN_SUCCESSFUL,"0");
			for(HashMap<String,String> error : errors)
			{
				new ErrorReport(error);
			}
		}
	}

	/**
	 * This will be called when this class has returned to the ui thread
	 * @param result the result that was returned from doWork
	 */
	protected abstract void handlePostExecute(Result result);

	/**
	 * override this method if you need to send any cached data into the server
	 */
	protected void sendCachedData()
	{
	}
}
