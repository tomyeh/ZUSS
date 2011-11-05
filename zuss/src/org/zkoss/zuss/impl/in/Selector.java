/* Selector.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 15:37:35 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.in;

/**
 * Represents a CSS selector.
 * @author tomyeh
 */
/*package*/ class Selector implements Token {
	private final String _value;
	private final int _lineno;

	public Selector(String value, int lineno) {
		_value = value;
		_lineno = lineno;
	}
	/** Returns the value.
	 */
	public String getValue() {
		return _value;
	}

	@Override
	public int getLine() {
		return _lineno;
	}
	@Override
	public String toString() {
		return _value;
	}
}
