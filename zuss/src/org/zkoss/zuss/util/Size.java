/* Size.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov  9 16:29:51 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.util;

/**
 * Represents a font size.
 * @author tomyeh
 */
public class Size {
	public final double size;
	public final String measure;

	public Size(String s) {
		int j = 0;
		boolean dot = false;
		for (int len = s.length(); j < len; ++j) {
			final char cc = s.charAt(j);
			if ((cc < '0' || cc > '9') && (dot || !(dot = cc == '.')))
				break;
		}

		size = j > 0 ? Double.parseDouble(s.substring(0, j)): 0;
		measure = j >= 0 ? s.substring(j): s;
	}

	/** Return a size that negates this size.
	 */
	public Size negate() {
		return this;
	}
	/** Returns a size that adds this size and the given object.
	 */
	public Size add(Object o) {
		return this;
	}
	/** Returns a size that subtracts this size and the given object.
	 */
	public Size subtract(Object o) {
		return this;
	}

	@Override
	public String toString() {
		return size + measure;
	}
	@Override
	public int hashCode() {
		return ((int)size) * measure.hashCode();
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof Size) {
			final Size sz = (Size)o;
			return sz.size == size && sz.measure.equals(measure);
		}
		return false;
	}
}
