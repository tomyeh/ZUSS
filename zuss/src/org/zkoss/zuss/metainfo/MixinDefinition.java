/* MixinDefinition.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 11:11:42 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents a mixin definition.
 * @author tomyeh
 */
public class MixinDefinition extends BranchInfo {
	private final String _name;
	private final ArgumentDefinition[] _args;

	public MixinDefinition(NodeInfo parent, String name,
	ArgumentDefinition[] args, int lineno) {
		super(parent, lineno);
		_name = name;
		_args = args;
	}

	/** Returns the name of this function definition.
	 */
	public String getName() {
		return _name;
	}
	/** Returns the argument definitions.
	 */
	public ArgumentDefinition[] getArgumentDefinitions() {
		return _args;
	}

	@Override
	public String toString() {
		return '@' + _name + "(mixin)";
	}
}
