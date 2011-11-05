/* ZussException.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 10:51:00 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss;

/**
 * Represents a ZUSS exception.
 * @author tomyeh
 */
public class ZussException extends RuntimeException {
	public ZussException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public ZussException(String msg) {
		super(msg);
	}
	public ZussException(Throwable cause) {
		super(cause);
	}
	public ZussException() {
	}
}
