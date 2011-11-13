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
	public ZussException(String msg, String filename, int lineno) {
		super(message(msg, filename, lineno));
	}
	public ZussException(String msg, String filename, int lineno, Throwable cause) {
		super(message(msg, filename, lineno), cause);
	}
	public ZussException(Throwable cause) {
		super(cause);
	}
	public ZussException() {
	}
	private static String message(String msg, String filename, int lineno) {
		final StringBuffer sb = new StringBuffer().append("[zuss] ");
		if (filename != null && filename.length() > 0)
			sb.append(filename).append(':');
		return sb.append("Line ").append(lineno).append(": ").append(msg).toString();
	}
}
