/* ServletContextLocator.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 11 15:33:34 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss;

import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.servlet.ServletContext;

/**
 * A locator for a servlet context.
 * @author tomyeh
 */
public class ServletContextLocator implements Locator {
	private final ServletContext _ctx;
	private final String _dir;
	private final String _charset;

	public ServletContextLocator(ServletContext ctx, String dir, String charset) {
		_ctx = ctx;
		if (dir == null) dir = "";
		if (dir.length() > 0 && dir.charAt(dir.length() - 1) != '/')
			dir += '/';
		_dir = dir;
		_charset = charset;
		
	}

	@Override
	public Reader getResource(String name)
	throws IOException {
		if (!name.startsWith("/"))
			name = _dir + name;
		final InputStream is = _ctx.getResourceAsStream(name);
		return is != null ? _charset != null ?
			new InputStreamReader(is, _charset): new InputStreamReader(is): null;

	}
}
