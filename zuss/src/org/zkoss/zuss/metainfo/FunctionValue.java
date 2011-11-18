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
	private final boolean _varLook;

	/**
	 * @param argc the number of arguments
	 */
	public FunctionValue(NodeInfo parent, String name, int argc, int lineno) {
		super(parent, lineno);
		_name = name;
		_argc = argc;
		_varLook = false;
	}
	/** Construct a function invocation that isn't specified with a pair of
	 * parenthesis.
	 */
	public FunctionValue(NodeInfo parent, String name, int lineno) {
		super(parent, lineno);
		_name = name;
		_argc = 0;
		_varLook = true;
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
	/** Returns if this function invocation is not specified with a pair of
	 * parenthesis.
	 */
	public boolean isVariableLook() {
		return _varLook;
	}

	@Override
	public String toString() {
		return '@' + _name + "(#" + _argc + ')';
	}
}
