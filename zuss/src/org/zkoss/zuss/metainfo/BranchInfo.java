/* BranchInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 10:55:09 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

import java.util.List;
import java.util.LinkedList;

/**
 * A skeleton used to implement a branch node that allows children.
 * @author tomyeh
 */
/*package*/ class BranchInfo extends LeafInfo {
	private List<NodeInfo> _children = new LinkedList<NodeInfo>();
	/*package*/ BranchInfo(int lineno) {
		super(lineno);
	}
	/*package*/ BranchInfo(NodeInfo parent, int lineno) {
		super(parent, lineno);
	}

	@Override
	public void appendChild(NodeInfo child) {
		NodeInfo oldp = child.getParent();
		if (oldp != null)
			oldp.removeChild(child);

		_children.add(child);
		((LeafInfo)child).setParentDirectly(this); //except root, all are LeafInfo
	}
	@Override
	public boolean removeChild(NodeInfo child) {
		if (child != null && _children.remove(child)) {
			((LeafInfo)child).setParentDirectly(null); //except root, all are LeafInfo
			return true;
		}
		return false;
	}
	@Override
	public List<NodeInfo> getChildren() {
		return _children;
	}
}
