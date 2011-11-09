/* Operators.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov  8 15:51:23 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.util;

/**
 * Utilities for handling CSS values, such as font size, color and so on.
 *
 * @author tomyeh
 */
public class Operators {
	/** Negates the given value. */
	public static Object negate(Object val) {
		return null;
	}
	/** Adds the given values. */
	public static Object add(Object val0, Object val1) {
		//TODO
		return "<" + val0 +"+" + val1 + ">";
	}
	/** Subtracts the given values. */
	public static Object subtract(Object val0, Object val1) {
		//TODO
		return "<" + val0 +"-" + val1 + ">";
	}
	/** Multiplies the given values. */
	public static Object multiply(Object val0, Object val1) {
		//TODO
		return "<" + val0 +"*" + val1 + ">";
	}
	/** Divides the given values. */
	public static Object divide(Object val0, Object val1) {
		//TODO
		return "<" + val0 +"/" + val1 + ">";
	}

	/** Test if the given values equal to each other. */
	public static boolean equals(Object val0, Object val1) {
		return false;
	}
	/** Compares the given values.
	 * @return 1 if val0 is greater, 0 if equals, -1 if smaller
	 */
	public static int compare(Object val0, Object val1) {
		return 0;
	}

	/** Tests if the given object is true (i.e., non-null, not empty, not false). */
	public static boolean isTrue(Object val0) {
		return true;
	}
}
