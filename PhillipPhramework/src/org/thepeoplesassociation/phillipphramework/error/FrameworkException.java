package org.thepeoplesassociation.phillipphramework.error;

public class FrameworkException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -196845341432929481L;
	
	public FrameworkException(String detailMessage){
		super(detailMessage);
	}
	
	public FrameworkException(String detailMessage, Throwable throwable){
		super(detailMessage,throwable);
	}
}
