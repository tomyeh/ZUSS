/* Other.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 18:25:05 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.in;

/**
 * A token other than {@link Id}, {@link Selector} and {@link Symbol}.
 * @author tomyeh
 */
/*package*/ class Other implements Token {
	private final String _value;
	private final int _lineno;

	public Other(String value, int lineno) {
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
