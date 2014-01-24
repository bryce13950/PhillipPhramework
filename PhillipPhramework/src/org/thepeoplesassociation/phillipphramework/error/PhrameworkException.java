package org.thepeoplesassociation.phillipphramework.error;

public class PhrameworkException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -196845341432929481L;
	
	public PhrameworkException(String detailMessage){
		super(detailMessage);
	}
	
	public PhrameworkException(String detailMessage, Throwable throwable){
		super(detailMessage,throwable);
	}
}
