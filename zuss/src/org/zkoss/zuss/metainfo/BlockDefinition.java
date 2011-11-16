/* BlockDefinition.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 15 12:03:37 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents a block with an optional condition.
 * It is used for implementing the multi-block statements
 * such as {@link IfDefinition}.
 * @author tomyeh
 */
public class BlockDefinition extends BranchInfo {
	private final Expression _cond;

	public BlockDefinition(NodeInfo parent, Expression cond, int lineno) {
		super(parent, lineno);
		_cond = cond;
	}
	/** Returns the condition.
	 */
	public Expression getCondition() {
		return _cond;
	}
}
