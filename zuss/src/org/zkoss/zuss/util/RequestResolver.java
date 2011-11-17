/* RequestResolver.java

	Purpose:
		
	Description:
		
	History:
		Sat Nov 12 00:31:18 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.util;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.zuss.Resolver;

/**
 * The resolver used to provide the builtin variables and functions.
 *
 * <p>Built-in variables include the request's attributes and parameters.
 * In additions, they include the variables identifiing a browser, such as
 * ie, gecko, opera, webkit, ff (the same as gecko) and safari (the same as gecko).
 * The browser variables are a double representing the version of the browser.
 * For example, <code>@if (@ie < 9) {...}</code>.
 *
 * @author tomyeh
 */
public class RequestResolver implements Resolver {
	/** A map of built-in variables, including ie, gecko, opera and webkit. */
	protected final Map<String, Object> _vars = new HashMap<String, Object>();
	/** The request. */
	protected final HttpServletRequest _request;
	
	private static final Pattern
		_rwebkit = Pattern.compile(".*(webkit)[ /]([\\w.]+).*"),
		_ropera = Pattern.compile(".*(opera)(?:.*version)?[ /]([\\w.]+).*"),
		_rmsie = Pattern.compile(".*(msie) ([\\w.]+).*"),
		_rmozilla = Pattern.compile(".*(mozilla)(?:.*? rv:([\\w.]+))?.*");

	public RequestResolver(HttpServletRequest request) {
		_request = request;
		parseUserAgent();
	}
	private void parseUserAgent() {
		final String ua = _request.getHeader("user-agent").toLowerCase();
		if (ua != null) {
			Matcher m = _rwebkit.matcher(ua);
			if(m.matches()) {
				final double version = getVersion(m);
				_vars.put("webkit", version);
				_vars.put("safari", version);
				return;
			}
			m = _ropera.matcher(ua);
			if (m.matches()) {
				_vars.put("opera", getVersion(m));
				return;
			}
			m = _rmsie.matcher(ua);
			if (m.matches()) {
				_vars.put("ie", getVersion(m));
				return;
			}
			if (ua.indexOf("compatible") < 0) {
				m = _rmozilla.matcher(ua);
				if (m.matches()) {
					final double version = getVersion(m);
					_vars.put("gecko", version);
					_vars.put("ff", version);
					return;
				}
			}
		}
	}
	private static double getVersion(Matcher m) {
		if (m.groupCount() < 2)
			return 1; //ignore it

		String version = m.group(2);
		int j = version.indexOf('.');
		if (j >= 0) {
			j = version.indexOf('.', j + 1);
			if (j >= 0)
				version = version.substring(0, j);
		}
		try {
			return Double.parseDouble(version);
		} catch (Throwable t) {
			return 1; //ignore it
		}
	}

	@Override
	public Object getVariable(String name) {
		if ("request".equals(name))
			return _request;
		Object o = _request.getAttribute(name);
		if (o != null)
			return o;
		o = _request.getParameter(name);
		if (o != null)
			return o;
		return _vars.get(name);
	}
	@Override
	public Method getMethod(String name) {
		return null;
	}
}
