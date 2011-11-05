/* StyleDefinition.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov  4 15:05:25 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents a CSS property (i.e., xxx: y1 y2).
 *
 * <p>The children are the values of this variable (i.e., y1 y2).
 * @author tomyeh
 */
public class StyleDefinition extends BranchInfo {
	private final String _name;

	public StyleDefinition(NodeInfo parent, String name, int lineno) {
		super(parent, lineno);
		_name = name;
	}
	/** Returns the name of the statement, i.e., CSS property's name.
	 */
	public String getName() {
		return _name;
	}

	@Override
	public String toString() {
		return "Style(" + _name + ')';
	}
}
