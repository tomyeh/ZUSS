/* NodeInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 10:35:42 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

import java.util.List;

/**
 * Represents a node in a ZUSS metainfo tree.
 * @author tomyeh
 */
public interface NodeInfo {
	/** Returns the parent, or null if it is the root.
	 */
	public NodeInfo getParent();
	/** Returns a readonly list of children.
	 *
	 * <p>Note: the returned list is readonly. To modify, please use
	 * {@link #appendChild} and {@link #removeChild} instead.
	 */
	public List<NodeInfo> getChildren();

	/** Append a child
	 */
	public void appendChild(NodeInfo child);
	/** Removes a child.
	 */
	public boolean removeChild(NodeInfo child);

	/** Returns the line number of this info.
	 */
	public int getLine();
}
