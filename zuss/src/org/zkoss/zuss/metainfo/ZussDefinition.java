/* ZussDefinition.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 11:04:30 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents the definiton of a ZUSS style sheet.
 * It is the root element of the metainfo tree of a ZUSS file.
 * @author tomyeh
 */
public class ZussDefinition extends BranchInfo {
	public ZussDefinition() {
		super(1);
	}

	@Override
	public String toString() {
		return "ZUSS@" + System.identityHashCode(this);
	}
}
