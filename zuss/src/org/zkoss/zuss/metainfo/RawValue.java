/* RawValue.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov 14 18:31:41 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents a value that shall be outputed to CSS directly.
 * @author tomyeh
 */
public class RawValue extends LeafInfo {
	private final String _value;

	/**
	 * @param value the content that shall be generated directly to the CSS output.
	 */
	public RawValue(NodeInfo parent, String value, int lineno) {
		super(parent, lineno);
		_value = value;
	}
	/** Returns the value that shall generated directly to the CSS output.
	 */
	public String getValue() {
		return _value;
	}

	@Override
	public String toString() {
		return _value;
	}
}
