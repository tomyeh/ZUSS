/* Locator.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov  4 10:03:52 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss;

/**
 * Custom resource locator.
 * @author tomyeh
 */
public interface Locator {
	/** Returns the resource of the given name.
	 */
	public java.io.Reader getResource(String name)
	throws java.io.IOException;
}
