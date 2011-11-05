/* VariableInvocation.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov  4 17:04:37 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents a variable invocation.
 * @author tomyeh
 */
public class VariableInvocation extends LeafInfo {
	private final String _name;

	public VariableInvocation(NodeInfo parent, String name, int lineno) {
		super(parent, lineno);
		_name = name;
	}
	/** Returns the variable's name.
	 */
	public String getName() {
		return _name;
	}

	@Override
	public String toString() {
		return '@' + _name;
	}
}
