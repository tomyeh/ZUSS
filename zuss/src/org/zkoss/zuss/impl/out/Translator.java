/* Translator.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 12:10:15 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.out;

import java.util.List;
import java.util.LinkedList;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.zuss.Resolver;
import org.zkoss.zuss.metainfo.NodeInfo;
import org.zkoss.zuss.metainfo.SheetDefinition;
import org.zkoss.zuss.metainfo.RuleDefinition;
import org.zkoss.zuss.metainfo.StyleDefinition;
import org.zkoss.zuss.metainfo.VariableDefinition;
import org.zkoss.zuss.metainfo.ConstantValue;
import org.zkoss.zuss.metainfo.VariableValue;
import org.zkoss.zuss.ZussException;

/**
 * The translator used to translate ZUSS to CSS.
 * @author tomyeh
 */
public class Translator {
	private final SheetDefinition _sheet;
	private final Writer _out;
	private final Resolver _resolver;

	public Translator(SheetDefinition sheet, Writer out, Resolver resolver) {
		_sheet = sheet;
		_out = out;
		_resolver = resolver;
	}
	/** Generates the CSS content.
	 * <p>Notice that this method can be called only once.
	 */
	public void translate() throws IOException {
		try {
			for (NodeInfo node: _sheet.getChildren()) {
				if (node instanceof RuleDefinition) {
					outRule(null, (RuleDefinition)node);
				} else {
					throw new ZussException("unknown "+node, node.getLine());
				}
			}
		} finally {
			try {
				_out.close();
			} catch (Throwable t) {
			}
		}
	}
	/**
	 * @param outerSels the selectors of enclosing rules.
	 * @param rdef the rule definition to generate
	 */
	private void outRule(List<String> outerSels, RuleDefinition rdef) throws IOException {
		final List<String> thisSels;
		if (outerSels == null) {
			thisSels = rdef.getSelectors();
		} else {
			thisSels = new LinkedList<String>();
			for (String s: rdef.getSelectors())
				for (String os: outerSels)
					thisSels.add(s.startsWith("&") ? os + s.substring(1): os + ' ' + s);
		}

		final String head = cat(thisSels) + "{\n", end = "}\n";
		boolean empty = true;
		for (NodeInfo node: rdef.getChildren()) {
			if (node instanceof RuleDefinition) {
				if (!empty) {
					empty = true;
					write(end);
				}
				outRule(thisSels, (RuleDefinition)node);
			} else if (node instanceof StyleDefinition) {
				if (empty) {
					empty = false;
					write(head);
				}
				outStyle((StyleDefinition)node);
			} else {
				//TODO
			}
		}
		if (!empty)
			write(end);
	}
	private void outStyle(StyleDefinition sdef) throws IOException {
		write(' ');
		write(sdef.getName());
		write(':');

		for (NodeInfo node: sdef.getChildren()) {
			if (node instanceof ConstantValue) {
				write(' ');
				write(((ConstantValue)node).getValue());
			} else if (node instanceof VariableValue) {
			} else {
				throw new ZussException("unknown "+node, node.getLine());
			}
		}

		write(";\n");
	}

	private void write(String s) throws IOException {
		_out.write(s);
	}
	private void write(char c) throws IOException {
		_out.write(c);
	}
	private static String cat(List<String> list) {
		final StringBuffer sb = new StringBuffer();
		for (String s: list) {
			if (sb.length() > 0) sb.append(',');
			sb.append(s);
		}
		return sb.toString();
	}
}
