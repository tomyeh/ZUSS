/* FunctionDefinition.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 11:10:22 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Represents a function definition.
 *
 * @author tomyeh
 */
public class FunctionDefinition extends LeafInfo {
	private final String _name;
	private final Expression _expr;
	private final Method _mtd;
	private final Map<String, String> _defValues;

	public FunctionDefinition(NodeInfo parent, String name, Expression expr,
	Map<String, String> defValues, int lineno) {
		super(parent, lineno);
		_name = name;
		_expr = expr;
		_mtd = null;
		_defValues = defValues;
	}
	public FunctionDefinition(NodeInfo parent, String name, Method mtd,
	Map<String, String> defValues, int lineno) {
		super(parent, lineno);
		_name = name;
		_expr = null;
		_mtd = mtd;
		_defValues = defValues;
	}

	/** Returns the expression, or null if this is a method ({@link #getMethod}).
	 */
	public Expression getExpression() {
		return _expr;
	}
	/** Returns the method, or null if this is an expression ({@link #getExpression}).
	 */
	public Method getMethod() {
		return _mtd;
	}

	@Override
	public String toString() {
		if (_expr != null)
			return '@' + _name + '(' + _expr + ')';
		return '@' + _name + '(' + _mtd + ')';
	}
}
