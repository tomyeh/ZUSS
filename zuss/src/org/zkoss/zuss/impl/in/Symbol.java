/* Symbol.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 14:52:09 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.in;

/**
 * A symbol, such as '{' and '}'.
 * @author tomyeh
 */
/*package*/ class Symbol implements Token {
	private final char _value;
	private final int _lineno;

	public Symbol(char value, int lineno) {
		_value = value;
		_lineno = lineno;
	}
	/** Returns the value of this symbol.
	 */
	public char getValue() {
		return _value;
	}

	@Override
	public int getLine() {
		return _lineno;
	}
	@Override
	public String toString() {
		return _value == '\n' ? "'\\n'": "'" + _value + '\'';
	}
}
