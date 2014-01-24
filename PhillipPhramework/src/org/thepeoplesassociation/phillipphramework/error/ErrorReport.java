package org.thepeoplesassociation.phillipphramework.error;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thepeoplesassociation.phillipphramework.PhrameworkApplication;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;


public class ErrorReport extends AsyncTask<Void,Void,Void>{

	private String Location, extraData;
	private Throwable thrown;
	
	private HashMap<String,String> cachedData;
	
	private long localId;
	
	public ErrorReport(Throwable t,String location, String extras){
		thrown=t;
		extraData = extras;
		Location=location;
		run();
	}
	
	public ErrorReport(HashMap<String,String> cached){
		cachedData = cached;
		Location = cachedData.get(TableError.COLUMN_LOCATION);
		run();
	}
	
	@SuppressLint("NewApi")
	private void run(){
		// start compatibility code
		if(VERSION.SDK_INT>=11){
			executeOnExecutor(THREAD_POOL_EXECUTOR);
		}
		else{
			execute();
		}
		// end compatibility code
		 
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		String url= PhrameworkApplication.instance.getErrorURL();
		try{
			HttpPost post=new HttpPost(url);
			ArrayList<NameValuePair> data=getError();
			post.setEntity(new UrlEncodedFormEntity(data));
	           
			DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setSoTimeout(httpClient.getParams(), 20000); 
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),20000); 
            
            HttpResponse response =httpClient.execute(post);
            HttpEntity ent = response.getEntity();
            PhrameworkApplication.logDebug("response"+EntityUtils.toString(ent));
            PhrameworkApplication.instance.getDatabase().update(TableError.NAME, TableError.COLUMN_SUCCESSFUL,"1", "id",""+localId);
            
		}
		catch(Exception e){
		}
		return null;
	}
	
	@SuppressLint("NewApi")
	private ArrayList<NameValuePair> getError() throws JSONException, NameNotFoundException{
		ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
		if(cachedData!=null){
			for(String key : cachedData.keySet()){
				data.add(new BasicNameValuePair(key,cachedData.get(key)));
			}
		}
		else{
			String[] columns = new String[]{
				TableError.COLUMN_ANDROID_VERSION,TableError.COLUMN_APPLICATION_VERSION, TableError.COLUMN_CLASS,
				TableError.COLUMN_EXTRAS, TableError.COLUMN_LOCALIZED, TableError.COLUMN_LOCATION,
				TableError.COLUMN_MESSAGE, TableError.COLUMN_STACK_TRACE, TableError.COLUMN_TIME
			};
			String[] values = new String[]{
				""+VERSION.SDK_INT, ""+PhrameworkApplication.instance.getPackageManager().getPackageInfo(PhrameworkApplication.instance.getPackageName(), 0).versionCode, thrown.getClass().toString(),
				extraData, thrown.getLocalizedMessage(), Location,
				thrown.getMessage(), getStackTrace(), "" + System.currentTimeMillis()
			};
			for(int i = 0; i < columns.length; i++){
				data.add(new BasicNameValuePair(columns[i], values[i]));
			}
			data.add(new BasicNameValuePair(TableError.COLUMN_SUCCESSFUL, "1"));
			localId = PhrameworkApplication.instance.getDatabase().Insert(TableError.NAME, columns, values);
		
		}
		data.add(new BasicNameValuePair("application", PhrameworkApplication.instance.getApplicationName()));
		String serial;
		if(VERSION.SDK_INT < 9)
			serial = "no_serial";
		else
			serial = Build.SERIAL;
		data.add(new BasicNameValuePair("serial", serial));
		return data;
	}
	
	public String getStackTrace() throws JSONException{
		
		StackTraceElement[] stack=thrown.getStackTrace();
		JSONArray trace=new JSONArray();
		
		JSONObject line;
		for(int i=0;i<stack.length;i++){
			
			line = new JSONObject();
			line.put("class", stack[i].getClassName());
			line.put("file", stack[i].getFileName());
			line.put("method", stack[i].getMethodName());
			line.put("line", stack[i].getLineNumber());
			trace.put(line);
		}
		
		return trace.toString();
	}
}