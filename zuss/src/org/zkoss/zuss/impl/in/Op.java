/* Op.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov  4 16:44:53 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.in;

import org.zkoss.zuss.metainfo.Operator.Type;

/**
 * A operator, such as '(', ')' and '+'.
 * @author tomyeh
 */
/*package*/ class Op implements Token {
	private Type _value;
	private final int _lineno;

	public Op(Type value, int lineno) {
		_value = value;
		_lineno = lineno;
	}
	/** Returns the value of this operator.
	 */
	public Type getValue() {
		return _value;
	}
	/** Sets the value of this operator.
	 */
	public void setValue(Type value) {
		_value = value;
	}

	@Override
	public int getLine() {
		return _lineno;
	}
	@Override
	public String toString() {
		return _value.toString();
	}
}
