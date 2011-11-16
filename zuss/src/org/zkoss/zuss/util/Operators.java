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
		val = guess(val);
		if (val instanceof Color)
			return ((Color)val).negate();
		if (val instanceof Size)
			return ((Size)val).negate();
		return val;
	}
	/** Adds the given values. */
	public static Object add(Object val0, Object val1) {
		val0 = guess(val0);
		val1 = guess(val1);
		if (val0 instanceof Color)
			return ((Color)val0).add(val1);
		if (val1 instanceof Color)
			return ((Color)val1).add(val0);
		if (val0 instanceof Size)
			return ((Size)val0).add(val1);
		if (val1 instanceof Size)
			return ((Size)val1).add(val0);
		if (val0 instanceof Number && val1 instanceof Number) {
			if (val0 instanceof Double || val1 instanceof Double)
				return ((Number)val0).doubleValue() + ((Number)val1).doubleValue();
			return ((Number)val0).intValue() + ((Number)val1).intValue();
		}
		return val0 == null ? val1: val1 == null ? val0: val0 +" " + val1;
	}
	/** Subtracts the given values. */
	public static Object subtract(Object val0, Object val1) {
		val0 = guess(val0);
		val1 = guess(val1);
		if (val0 instanceof Color)
			return ((Color)val0).subtract(val1);
		if (val0 instanceof Size)
			return ((Size)val0).subtract(val1);
		if (val0 instanceof Number && val1 instanceof Number) {
			if (val0 instanceof Double || val1 instanceof Double)
				return ((Number)val0).doubleValue() - ((Number)val1).doubleValue();
			return ((Number)val0).intValue() - ((Number)val1).intValue();
		}
		return val0 == null ? null: val1 == null ? val0:
			val0.toString().replace(val1.toString(), "");
	}
	/** Multiplies the given values. */
	public static Object multiply(Object val0, Object val1) {
		val0 = guess(val0);
		val1 = guess(val1);
		if (val0 instanceof Color)
			return ((Color)val0).multiply(val1);
		if (val1 instanceof Color)
			return ((Color)val1).multiply(val0);
		if (val0 instanceof Size)
			return ((Size)val0).multiply(val1);
		if (val1 instanceof Size)
			return ((Size)val1).multiply(val0);
		if (val0 instanceof Number && val1 instanceof Number) {
			if (val0 instanceof Double || val1 instanceof Double)
				return ((Number)val0).doubleValue() * ((Number)val1).doubleValue();
			return ((Number)val0).intValue() * ((Number)val1).intValue();
		}
		return (val0 != null ? val0: "") + "*" + (val1 != null ? val1: "");
	}
	/** Divides the given values. */
	public static Object divide(Object val0, Object val1) {
		val0 = guess(val0);
		val1 = guess(val1);
		if (val0 instanceof Color)
			return ((Color)val0).divide(val1);
		if (val0 instanceof Size)
			return ((Size)val0).divide(val1);
		if (val0 instanceof Number && val1 instanceof Number) {
			if (val0 instanceof Double || val1 instanceof Double)
				return ((Number)val0).doubleValue() / ((Number)val1).doubleValue();
			return ((Number)val0).intValue() / ((Number)val1).intValue();
		}
		return (val0 != null ? val0: "") + "/" + (val1 != null ? val1: "");
	}

	/** Test if the given values equal to each other. */
	public static boolean equals(Object val0, Object val1) {
		val0 = guess(val0);
		val1 = guess(val1);
		return val0 == val1 || (val0 != null && val1 != null && val0.equals(val1));
	}
	/** Compares the given values.
	 * @return 1 if val0 is greater, 0 if equals, -1 if smaller
	 */
	public static int compare(Object val0, Object val1) {
		val0 = guess(val0);
		val1 = guess(val1);
		if (val0 instanceof Color)
			return ((Color)val0).compareTo(val1);
		if (val1 instanceof Color)
			return -((Color)val1).compareTo(val0);
		if (val0 instanceof Size)
			return ((Size)val0).compareTo(val1);
		if (val1 instanceof Size)
			return -((Size)val1).compareTo(val0);
		if (val0 instanceof Number && val1 instanceof Number) {
			final double v0 = ((Number)val0).doubleValue(),
				v1 = ((Number)val1).doubleValue();
			return v0 > v1 ? 1: v0 == v1 ? 0: -1;
		}
		return val0 != null ? val1 != null ?
			val0.toString().compareTo(val1.toString()): 1: val1 != null ? -1: 0;
	}

	/** Tests if the given object is true (i.e., non-null, not empty, not false). */
	public static boolean isTrue(Object val0) {
		final Boolean b = (Boolean)Classes.coerce(Boolean.class, val0);
		return b != null && b.booleanValue();
	}

	/** Guess what a string means. */
	private static Object guess(Object val) {
		if (val instanceof String) {
			String s = ((String)val).trim();
			if (s.length() > 0) {
				final char cc = s.charAt(0);
				if (cc == '#')
					return new Color(s);

				if (cc >= '0' && cc <= '9') {
					final boolean percent = s.charAt(s.length() - 1) == '%';
					if (percent) {
						s = s.substring(0, s.length() - 1);
					} else {
						try {
							return new Integer(s);
						} catch (NumberFormatException ex) {
						}
					}
					if (percent || s.indexOf('.') >= 0)
						try {
							final Double d = new Double(s);
							return percent ? d / 100: d;
						} catch (NumberFormatException ex2) {
						}
					return new Size(s);
				}

				Color c = Color.getStandardColor(s);
				if (c != null)
					return c;
			}
		}
		return val;
	}
}
