/* VariableDefinition.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 11:09:26 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents a variable definition (@xx: y1 y2).
 *
 * <p>The children are the values of this variable (i.e., y1 y2).
 * @author tomyeh
 */
public class VariableDefinition extends LeafInfo {
	private final String _name;
	private final Expression _expr;

	public VariableDefinition(NodeInfo parent, String name,
	Expression expr, int lineno) {
		super(parent, lineno);
		_name = name;
		_expr = expr;
	}

	/** Returns the variable's name.
	 */
	public String getName() {
		return _name;
	}

	/** Returns the variable's expression.
	 */
	public Expression getExpression() {
		return _expr;
	}

	@Override
	public String toString() {
		return "@" + _name + ':' + _expr;
	}
}
