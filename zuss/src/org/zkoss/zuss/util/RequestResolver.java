/* RequestResolver.java

	Purpose:
		
	Description:
		
	History:
		Sat Nov 12 00:31:18 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.util;

import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.zuss.Resolver;

/**
 * The resolver used to provide the builtin variables and functions,
 * such as @param(name).
 *
 * @author tomyeh
 */
public class RequestResolver implements Resolver {
	private final HttpServletRequest _request;

	public RequestResolver(HttpServletRequest request) {
		_request = request;
	}

	@Override
	public Object getVariable(String name) {
		if ("request".equals(name))
			return _request;
		Object o = _request.getAttribute(name);
		if (o != null)
			return o;
		return _request.getParameter(name);
	}
	@Override
	public Method getMethod(String name) {
		return null;
	}
}
