package org.thepeoplesassociation.phillipphramework.communication;

import java.util.HashMap;
import java.util.List;

import org.thepeoplesassociation.phillipphramework.FrameworkApplication;
import org.thepeoplesassociation.phillipphramework.error.ErrorReport;
import org.thepeoplesassociation.phillipphramework.error.FrameworkExceptionHandler;
import org.thepeoplesassociation.phillipphramework.error.TableError;

import android.os.AsyncTask;


public abstract class FrameworkAsyncTask<Params,Progress,Result> extends AsyncTask<Params,Progress,Result>{

	protected String ThreadName;
	
	protected FrameworkAsyncTask(String name){
		ThreadName=name;
	}
	
	protected Result doInBackground(Params... params){
		Thread.setDefaultUncaughtExceptionHandler(new FrameworkExceptionHandler(ThreadName,Thread.getDefaultUncaughtExceptionHandler()));
		return doWork(params);
	}
	
	protected abstract Result doWork(Params... params);
	
	protected void onPostExecute(Result result){
		handlePostExecute(result);
		sendCachedData();
		if(FrameworkApplication.instance != null && FrameworkApplication.instance.getDatabase() != null){
			List<HashMap<String,String>> errors=FrameworkApplication.instance.getDatabase().getTableWhere(TableError.NAME,TableError.COLUMN_SUCCESSFUL,"0");
			for(HashMap<String,String> error:errors){
				new ErrorReport(error);
			}
		}
	}
	
	protected abstract void handlePostExecute(Result result);
	
	protected void sendCachedData(){}
}
