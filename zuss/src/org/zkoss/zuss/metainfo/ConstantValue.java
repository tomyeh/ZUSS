/* ConstantValue.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov  4 15:12:24 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents a value, i.e., the value on the right side of colon.
 * @author tomyeh
 */
public class ConstantValue extends LeafInfo {
	private final String _value;

	public ConstantValue(NodeInfo parent, String value, int lineno) {
		super(parent, lineno);
		_value = value;
	}

	/** Returns the value.
	 */
	public String getValue() {
		return _value;
	}

	@Override
	public String toString() {
		return "Value(" + _value + ')';
	}
}
