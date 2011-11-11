/* RuleDefinition.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 11:12:25 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

import java.util.List;
import java.util.LinkedList;

/**
 * Represents the defintion of a CSS rule.
 *
 * @author tomyeh
 */
public class RuleDefinition extends BranchInfo {
	private final List<String> _sels = new LinkedList<String>();

	public RuleDefinition(NodeInfo parent, int lineno) {
		super(parent, lineno);
	}

	/** Returns the selectors of this rule.
	 */
	public List<String> getSelectors() {
		return _sels;
	}

	@Override
	public String toString() {
		return "Rule(" + _sels + ')';
	}
}
