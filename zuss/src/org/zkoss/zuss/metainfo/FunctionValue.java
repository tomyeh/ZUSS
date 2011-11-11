/* FunctionValue.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov  8 11:31:09 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents a function invocation.
 * @author tomyeh
 */
public class FunctionValue extends LeafInfo {
	private final String _name;
	/** The number of arugments. */
	private final int _argc;

	/**
	 * @param argc the number of arguments
	 */
	public FunctionValue(NodeInfo parent, String name, int argc, int lineno) {
		super(parent, lineno);
		_name = name;
		_argc = argc;
	}
	/** Returns the variable's name.
	 */
	public String getName() {
		return _name;
	}
	/** Returns the number of arguments passed to this invocation.
	 */
	public int getArgumentNumber() {
		return _argc;
	}

	@Override
	public String toString() {
		return '@' + _name + "(#" + _argc + ')';
	}
}
