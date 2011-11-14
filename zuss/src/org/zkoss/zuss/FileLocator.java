/* FileLocator.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov  4 18:34:47 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss;

import java.io.File;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * A file-based locator.
 *
 * @author tomyeh
 */
public class FileLocator implements Locator {
	private final File _dir;
	private final String _charset;

	/** Construct a file locator.
	 * @param dir the directory. If null, the current directory is assumed.
	 */
	public FileLocator(File dir, String charset) {
		_dir = dir != null ? dir: new File("");
		_charset = charset;
	}
	@Override
	public Reader getResource(String name)
	throws IOException {
		File file = new File(name);
		final InputStream is = new java.io.FileInputStream(
			file.isAbsolute() ? file: new File(_dir, name));
		return _charset != null ?
			new InputStreamReader(is, _charset): new InputStreamReader(is);
	}
}
