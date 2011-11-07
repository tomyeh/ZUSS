/* Expression.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov  7 12:06:25 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents an expression.
 *
 * <p>The children is a list of values and operators in the postfix expression.
 * @author tomyeh
 */
public class Expression extends BranchInfo {
	public Expression(NodeInfo parent, int lineno) {
		super(parent, lineno);
	}

	@Override
	public String toString() {
		return "Expression(" + getChildren() + ')';
	}
}
