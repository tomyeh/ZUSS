/* MediaDefinition.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov 14 19:36:26 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents CSS's @media definition.
 * @author tomyeh
 */
public class MediaDefinition extends BranchInfo {
	private final String _range;

	public MediaDefinition(NodeInfo parent, String range, int lineno) {
		super(parent, lineno);
		_range = range;
	}
	/** Returns the range of this media statement.
	 */
	public String getRange() {
		return _range;
	}

	@Override
	public String toString() {
		return "@media " + _range;
	}
}
