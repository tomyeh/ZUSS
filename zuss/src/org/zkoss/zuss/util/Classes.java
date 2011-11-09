/* Classes.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov  9 15:23:52 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.util;

/**
 * Class related utilities.
 * @author tomyeh
 */
public class Classes {
	/** Converts an object to the specified class.
	 * It is the same as coerce(cls, val, true).
	 *
	 * @param val the value to convert
	 * @exception ClassCastException if failed to convert
	 */
	public static Object coerce(Class<?> cls, Object val)
	throws ClassCastException {
		return val;
	}
}
