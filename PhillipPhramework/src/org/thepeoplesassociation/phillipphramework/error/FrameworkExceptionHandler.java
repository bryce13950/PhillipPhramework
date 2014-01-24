package org.thepeoplesassociation.phillipphramework.error;

import java.lang.Thread.UncaughtExceptionHandler;

import org.thepeoplesassociation.phillipphramework.FrameworkApplication;


public class FrameworkExceptionHandler implements UncaughtExceptionHandler{
private String name;
	
	private UncaughtExceptionHandler handle;
	public FrameworkExceptionHandler(String n, UncaughtExceptionHandler h){
		name=n;
		handle=h;
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		FrameworkApplication.handleCaughtException(ex, name);
		handle.uncaughtException(thread, ex);
	}
}
