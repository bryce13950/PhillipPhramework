package org.axolotlinteractive.phillipphramework.error;

import java.lang.Thread.UncaughtExceptionHandler;

import org.axolotlinteractive.phillipphramework.PhrameworkApplication;


public class PhrameworkExceptionHandler implements UncaughtExceptionHandler{
private String name;
	
	private UncaughtExceptionHandler handle;
	public PhrameworkExceptionHandler(String n, UncaughtExceptionHandler h){
		name=n;
		handle=h;
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		PhrameworkApplication.handleCaughtException(ex, name);
		handle.uncaughtException(thread, ex);
	}
}
