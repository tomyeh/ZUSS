/* Zuss.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 11:16:15 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.zuss.metainfo.ZussDefinition;
import org.zkoss.zuss.impl.in.Parser;
import org.zkoss.zuss.impl.out.Translator;

/**
 * The ZUSS utilities.
 * @author tomyeh
 */
public class Zuss {
	/** Parses from the given file.
	 *
	 * @param file the input stream to read ZUSS from
	 * @param charset the encoding used in the input stream, such as "UTF-8".
	 * If null, the system default is used.
	 */
	public static ZussDefinition parse(File file, String charset)
	throws IOException {
		return parse(new java.io.FileInputStream(file),
			charset, new FileLocator(file.getParentFile(), charset));
	}
	/** Parses from the given input stream.
	 * @param in the input stream to read ZUSS from
	 * @param charset the encoding used in the file, such as "UTF-8".
	 * If null, the system default is used.
	 * @param loc the locator used to locate the resource included by @include.
	 * It can't be null if @include is used.
	 */
	public static ZussDefinition parse(InputStream in, String charset, Locator loc)
	throws IOException {
		return parse(
			charset != null ? new InputStreamReader(in, charset):
				new InputStreamReader(in), loc);
	}
	/** Parses from the given reader.
	 * @param in the reader to read ZUSS from
	 * @param loc the locator used to locate the resource included by @include.
	 * It can't be null if @include is used.
	 */
	public static ZussDefinition parse(Reader in, Locator loc)
	throws IOException {
		return new Parser(in, loc).parse();
	}

	/** Translate ZUSS to CSS, and store to the gieven file based on the given ZUL style sheet.
	 * <p>Notice that if the file exists, it will be overwritten.
	 * @param def the ZUL style style (source)
	 * @param file the file to store the generated CSS content.
	 * @param charset the encoding used in the file, such as "UTF-8".
	 * If null, the system default is used.
	 * @param resolver the custom variable and function resolver. Ignored if null.
	 */
	public static void translate(ZussDefinition def, File file, String charset,
	Resolver resolver) throws IOException {
		translate(def,
			new java.io.BufferedOutputStream(new java.io.FileOutputStream(file)),
			charset, resolver);
	}
	/** Translate ZUSS to CSS, and store CSS to the gieven output based on the given ZUL style sheet.
	 * @param def the ZUL style style (source)
	 * @param out the output stream to store the generated CSS content.
	 * @param charset the encoding used in the file, such as "UTF-8".
	 * If null, the system default is used.
	 * @param resolver the custom variable and function resolver. Ignored if null.
	 */
	public static void translate(ZussDefinition def, OutputStream out,
	String charset, Resolver resolver) throws IOException {
		translate(def,
			charset != null ? new OutputStreamWriter(out, charset):
				new OutputStreamWriter(out), resolver);
	}
	/** Translate ZUSS to CSS, and store to the gieven output based on the given ZUL style sheet.
	 * @param def the ZUL style style (source)
	 * @param out the writer to store the generated CSS content.
	 * @param resolver the custom variable and function resolver. Ignored if null.
	 */
	public static void translate(ZussDefinition def, Writer out, Resolver resolver)
	throws IOException {
		new Translator(def, out, resolver).translate();
	}

	public static void main(String[] args) throws IOException {
		String charset = null;
		File in = null, out = null;
		boolean overwrite = false;
		for (int j = 0; j < args.length; ++j) {
			if ("-help".equals(args[j]) || "-h".equals(args[j])) {
				System.out.println(USAGE
					+ "where possible options include:\n"
					+"  -h -help               Pring a synopsis of standard options\n"
					+"  -encoding <encoding>   Specify character encoding\n"
					+"  -o -overwrite          Overwrite the output file if exists\n");
				return;

			} else if ("-encoding".equals(args[j])) {
				if (++j >= args.length) {
					error("-encoding requires an argument");
					return;
				}
				charset = args[j];

			} else if ("-o".equals(args[j]) || "-overwrite".equals(args[j])) {
				overwrite = true;
			} else if (args[j].startsWith("-")) {
				error("Unrecognized option: "+args[j]);
				return;
			} else {
				if (in == null) {
					in = new File(args[j]);
					if (!in.exists()) {
						error("file not found: "+args[j]);
						return;
					}
				} else if (out == null) {
					out = new File(args[j]);
					if (!overwrite && out.exists()) {
						error("file exists: "+args[j]);
						return;
					}
				}
			}
		}

		final ZussDefinition def =
			in != null ? parse(in, charset):
				parse(System.in, charset, new FileLocator(null, charset));

		if (out != null) {
			translate(def, out, charset, null);
		} else {
			translate(def, System.out, charset, null);
		}
	}
	private static void error(String msg) {
		System.err.println("zuss: " + msg + '\n' + USAGE
			+ "use -help for a list of possible options");
	}
	private static final String USAGE = "Usage: zuss <options> [<input-file>] [<output-file>]\n";
}
