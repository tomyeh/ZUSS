/* ArgumentDefinition.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov  8 18:40:05 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents the defintion of arguments of {@link Mixin} and {@link FunctionDefinition}.
 * @author tomyeh
 */
public class ArgumentDefinition extends LeafInfo {
	private final String _name;
	private final String _defValue;
	public ArgumentDefinition(String name, String defValue, int lineno) {
		super(lineno);
		_name = name;
		_defValue = defValue;
	}

	/** Returns the argument's name.
	 */
	public String getName() {
		return _name;
	}
	/** Returns the argument's default value.
	 */
	public String getDefaultValue() {
		return _defValue;
	}

	@Override
	public String toString() {
		return _name + ':' + _defValue;
	}
}
