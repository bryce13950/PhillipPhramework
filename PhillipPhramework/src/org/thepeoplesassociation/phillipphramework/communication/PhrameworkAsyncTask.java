package org.thepeoplesassociation.phillipphramework.communication;

import java.util.HashMap;
import java.util.List;

import org.thepeoplesassociation.phillipphramework.PhrameworkApplication;
import org.thepeoplesassociation.phillipphramework.error.ErrorReport;
import org.thepeoplesassociation.phillipphramework.error.PhrameworkExceptionHandler;
import org.thepeoplesassociation.phillipphramework.error.TableError;

import android.os.AsyncTask;


public abstract class PhrameworkAsyncTask<Params,Progress,Result> extends AsyncTask<Params,Progress,Result>{

	protected String ThreadName;
	
	protected PhrameworkAsyncTask(String name){
		ThreadName=name;
	}
	
	protected Result doInBackground(Params... params){
		Thread.setDefaultUncaughtExceptionHandler(new PhrameworkExceptionHandler(ThreadName,Thread.getDefaultUncaughtExceptionHandler()));
		return doWork(params);
	}
	
	protected abstract Result doWork(Params... params);
	
	protected void onPostExecute(Result result){
		handlePostExecute(result);
		sendCachedData();
		if(PhrameworkApplication.instance != null && PhrameworkApplication.instance.getDatabase() != null){
			List<HashMap<String,String>> errors=PhrameworkApplication.instance.getDatabase().getTableWhere(TableError.NAME,TableError.COLUMN_SUCCESSFUL,"0");
			for(HashMap<String,String> error:errors){
				new ErrorReport(error);
			}
		}
	}
	
	protected abstract void handlePostExecute(Result result);
	
	protected void sendCachedData(){}
}
