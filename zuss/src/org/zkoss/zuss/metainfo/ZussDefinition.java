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
	private final String _filename;

	public ZussDefinition(String filename) {
		super(1);
		_filename = filename;
	}

	/** Returns the filename of ZUSS that this definition represents,
	 * or null if not available.
	 */
	public String getFilename() {
		return _filename;
	}

	@Override
	public String toString() {
		return "ZUSS@" + System.identityHashCode(this);
	}
}
