/* Classes.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov  9 14:38:19 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.in;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.zkoss.zuss.ZussException;

/**
 * Class related utilities.
 * @author tomyeh
 */
/*private*/ class Classes {
	/** Returns the method of the given name.
	 * @param clsnm the class name and, optionally, a method name.
	 */
	public static Method getMethod(String clsnm, String mtdnm, int argc, int lineno) {
		int j = clsnm.lastIndexOf('#');
		if (j >= 0) {
			mtdnm = clsnm.substring(j + 1);
			if (mtdnm.length() == 0)
				throw new ZussException("a method name required after #", lineno);
			clsnm = clsnm.substring(0, j);
			if (clsnm.length() == 0)
				throw new ZussException("a class name required before #", lineno);
		}

		Class<?> cls = getClass(clsnm, lineno);
		Class<?>[] argTypes = new Class<?>[argc];
		for (j = argTypes.length; --j >= 0;)
			argTypes[j] = String.class;

		try {
			final Method m = getMethodInPublic(cls, mtdnm, argTypes, lineno);
			if ((m.getModifiers() & Modifier.STATIC) == 0)
				throw new ZussException("not a static method: "+m, lineno);
			return m;
		} catch (NoSuchMethodException ex) { //ignore
		}

		final Method[] ms = cls.getMethods();
		for (j = ms.length; --j >= 0;)
			if (mtdnm.equals(ms[j].getName())) {
				argTypes = ms[j].getParameterTypes();
				if (argTypes.length == argc)
					try {
						return getMethodInPublic(cls, mtdnm, argTypes, lineno);
					} catch (NoSuchMethodException ex) { //ignore
					}
			}
		throw new ZussException("method not found, "+mtdnm+", in "+cls, lineno);
	}
	private static Method getMethodInPublic(
	Class<?> cls, String mtdnm, Class<?>[] argTypes, int lineno)
	throws NoSuchMethodException {
		final Method m = cls.getMethod(mtdnm, argTypes);
		if (Modifier.isPublic(m.getDeclaringClass().getModifiers()))
			return m;

		final Class<?>[] clses = cls.getInterfaces();
		for (int j = 0; j< clses.length; ++j)
			try {
				return getMethodInPublic(clses[j], mtdnm, argTypes, lineno);
			} catch (NoSuchMethodException ex) { //ignore it
			}

		final Class<?> basecls = cls.getSuperclass();
		if (basecls != null)
			return getMethodInPublic(basecls, mtdnm, argTypes, lineno);

		throw new  ZussException("public method required: "+m, lineno);
	}

	/** Returns the class of the given name.
	 */
	public static Class<?> getClass(String clsnm, int lineno) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl != null)
			try {
				return Class.forName(clsnm, true, cl);
			} catch (ClassNotFoundException ex) { //ignore and try the other
			}
		try {
			return Parser.class.forName(clsnm);
		} catch (ClassNotFoundException ex) {
			throw new ZussException("class not found, "+clsnm, lineno);
		}
	}
}
