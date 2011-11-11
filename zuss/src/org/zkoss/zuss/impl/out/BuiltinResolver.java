/* BuiltinResolver.java

	Purpose:
		
	Description:
		
	History:
		Sat Nov 12 00:15:28 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.out;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

import org.zkoss.zuss.Resolver;
import org.zkoss.zuss.ZussException;

/**
 * The resolver providing the built-in variables and functions.
 * @author tomyeh
 */
public class BuiltinResolver implements Resolver {
	private final Map<String, Method> _funcs = new HashMap<String, Method>();

	public BuiltinResolver() {
		try {
			final Class<?> cls = BuiltinResolver.class;
			_funcs.put("eval",
				cls.getMethod("eval", new Class[] {Object.class}));
		} catch (NoSuchMethodException ex) {
			throw new ZussException(ex);
		}
	}

	@Override
	public Object getVariable(String name) {
		return null;
	}
	@Override
	public Method getMethod(String name) {
		return _funcs.get(name);
	}
	/** eval(Object val).
	 */
	public static Object eval(Object val) {
		return val;
	}
}
