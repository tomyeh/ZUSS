/* Id.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 15:00:16 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.in;

/**
 * Represents an identifier (@xxx).
 * @author tomyeh
 */
/*package*/ class Id implements Token {
	private final String _value;
	private final int _lineno;

	public Id(String value, int lineno) {
		_value = value;
		_lineno = lineno;
	}
	/** Returns the value (i.e., ID).
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
