/* Op.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov  7 12:08:21 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents an operator.
 *
 * @author tomyeh
 */
public class Op extends LeafInfo {
	private Type _type;
	public Op(NodeInfo parent, Type type, int lineno) {
		super(parent, lineno);
		_type = type;
	}

	/** Returns the type of the operator.
	 */
	public Type getType() {
		return _type;
	}

	@Override
	public String toString() {
		return "" + _type;
	}

	public static enum Type {
		MINUS, ADD, SUBTRACT, MULTIPLY, DIVIDE;
	}
}
