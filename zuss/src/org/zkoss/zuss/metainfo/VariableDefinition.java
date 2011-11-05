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
public class VariableDefinition extends BranchInfo {
	public VariableDefinition(NodeInfo parent, int lineno) {
		super(parent, lineno);
	}
}
