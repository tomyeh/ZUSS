/* Resolver.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov  4 10:04:04 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss;

import java.lang.reflect.Method;

/**
 * Custom variable and function resolver.
 * @author tomyeh
 */
public interface Resolver {
	/** Returns the value of the given name, or null if not available. 
	 */
	public Object getVariable(String name);
	/** Returns the method of the given name, or null if not available.
	 */
	public Method getMethod(String name);
}
