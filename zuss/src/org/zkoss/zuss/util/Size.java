/* Size.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov  9 16:29:51 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.util;

import org.zkoss.zuss.ZussException;

/**
 * Represents a font size.
 * @author tomyeh
 */
public class Size implements Comparable<Object> {
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
	public Size(double size, String measure) {
		this.size = size;
		this.measure = measure;
	}

	/** Returns an integer representing this size.
	 */
	public double getValue() {
		return size;
	}

	/** Return a size that negates this size.
	 */
	public Size negate() {
		throw new ZussException("Unable to negate a size, "+this);
	}
	/** Returns a size that adds this size and the given object.
	 */
	public Size add(Object o) {
		if (o instanceof Number)
			return new Size(size + ((Number)o).doubleValue(), measure);
		if (o instanceof Size)
			return new Size(size + ((Size)o).size, measure);
		throw new ZussException("Unable to add "+this+" with "+o);
	}
	/** Returns a size that subtracts this size and the given object.
	 */
	public Size subtract(Object o) {
		if (o instanceof Number)
			return new Size(size - ((Number)o).doubleValue(), measure);
		if (o instanceof Size)
			return new Size(size - ((Size)o).size, measure);
		throw new ZussException("Unable to subtract "+this+" with "+o);
	}
	/** Returns a size that multiplies this size with the given object.
	 */
	public Size multiply(Object o) {
		if (o instanceof Number)
			return new Size(round(size * ((Number)o).doubleValue()), measure);
		if (o instanceof Size)
			return new Size(round(size * ((Size)o).size), measure);
		throw new ZussException("Unable to multiply "+this+" with "+o);
	}
	/** Returns a size that divides this size with the given object.
	 */
	public Size divide(Object o) {
		if (o instanceof Number)
			return new Size(round(size / ((Number)o).doubleValue()), measure);
		if (o instanceof Size)
			return new Size(round(size / ((Size)o).size), measure);
		throw new ZussException("Unable to divide "+this+" with "+o);
	}
	private static double round(double val) {
		return Math.round(val * 10) / 10.0; //only one digit
	}

	/** Compares with another object
	 */
	@Override
	public int compareTo(Object o) {
		if (o instanceof Size || o instanceof Number) {
			double v1 = getValue(),
				v2 = o instanceof Number ?
					((Number)o).doubleValue(): ((Size)o).getValue();
			return v1 > v2 ? 1: v1 == v2 ? 0: -1;
		}
		if (o == null)
			return 1;
		throw new ZussException("Unable to compare "+this+" with "+o);
	}

	@Override
	public String toString() {
		String s = Double.toString(size);
		for (int j = s.length(); --j >= 0;) { //removing ending .0
			final char cc = s.charAt(j);
			if (cc != '0') {
				s = s.substring(0, cc == '.' ? j: j + 1);
				break;
			}
		}
		return s + measure;
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
