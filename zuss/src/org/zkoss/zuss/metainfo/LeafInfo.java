/* LeafInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 10:45:16 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

import java.util.List;
import java.util.Collections;

import org.zkoss.zuss.ZussException;

/**
 * A skeleton used to implement a leaf node that does not allow any children.
 * <p>Notice that it is serializable.
 * Also notice that the implementation assumes all nodes except the root
 * must extend from {@link LeafInfo} or its derives.
 * 
 * @author tomyeh
 */
/*package*/ class LeafInfo implements NodeInfo, java.io.Serializable {
	private NodeInfo _parent;
	private int _lineno;

	/*package*/ LeafInfo(int lineno) {
		_lineno = lineno;
	}
	/*package*/ LeafInfo(NodeInfo parent, int lineno) {
		this(lineno);
		parent.appendChild(this);
	}

	@Override
	public int getLine() {
		return _lineno;
	}
	@Override
	public NodeInfo getParent() {
		return _parent;
	}
	@Override
	public List<NodeInfo> getChildren() {
		return Collections.emptyList();
	}

	/** Sets a parent directly without maintaining the parent/child relationship.
	 */
	/*package*/ void setParentDirectly(NodeInfo parent) {
		_parent = parent;
	}

	@Override
	public void appendChild(NodeInfo child) {
		throw new ZussException(this+" does not allow any children");
	}
	@Override
	public boolean removeChild(NodeInfo child) {
		return false;
	}
}
