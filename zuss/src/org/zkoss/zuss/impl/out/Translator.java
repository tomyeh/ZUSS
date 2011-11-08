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
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.zuss.Resolver;
import org.zkoss.zuss.metainfo.NodeInfo;
import org.zkoss.zuss.metainfo.SheetDefinition;
import org.zkoss.zuss.metainfo.RuleDefinition;
import org.zkoss.zuss.metainfo.StyleDefinition;
import org.zkoss.zuss.metainfo.VariableDefinition;
import org.zkoss.zuss.metainfo.FunctionDefinition;
import org.zkoss.zuss.metainfo.Expression;
import org.zkoss.zuss.metainfo.FunctionValue;
import org.zkoss.zuss.metainfo.ConstantValue;
import org.zkoss.zuss.metainfo.VariableValue;
import org.zkoss.zuss.metainfo.Operator;
import static org.zkoss.zuss.metainfo.Operator.Type.*;
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
			out(new Scope(null), _sheet);
		} finally {
			try {
				_out.close();
			} catch (Throwable t) {
			}
		}
	}
	private void out(Scope scope, NodeInfo parent) throws IOException {
		for (NodeInfo node: parent.getChildren()) {
			if (node instanceof RuleDefinition) {
				outRule(new Scope(scope), null, (RuleDefinition)node);
			} else if (node instanceof VariableDefinition) {
				scope.put((VariableDefinition)node);
			} else if (node instanceof FunctionDefinition) {
				scope.put((FunctionDefinition)node);
			} else {
				throw new ZussException("unknown "+node, node.getLine());
			}
		}
	}
	/**
	 * @param outerSels the selectors of enclosing rules.
	 * @param rdef the rule definition to generate
	 */
	private void outRule(Scope scope, List<String> outerSels, RuleDefinition rdef)
	throws IOException {
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
				outRule(scope, thisSels, (RuleDefinition)node);
			} else if (node instanceof StyleDefinition) {
				if (empty) {
					empty = false;
					write(head);
				}
				outStyle(scope, (StyleDefinition)node);
			} else {
				//TODO
			}
		}
		if (!empty)
			write(end);
	}
	private void outStyle(Scope scope, StyleDefinition sdef) throws IOException {
		write('\t');
		write(sdef.getName());
		write(':');

		for (NodeInfo node: sdef.getChildren()) {
			final Object value = eval(scope, node);
			if (value != null) {
				write(' ');
				write(value.toString());
			}
		}

		write(";\n");
	}

	private Object eval(Scope scope, NodeInfo node) {
		if (node instanceof ConstantValue)
			return ((ConstantValue)node).getValue();
		if (node instanceof VariableValue)
			return eval(scope, (VariableValue)node);
		else if (node instanceof Expression)
			return eval(scope, (Expression)node);
		throw new ZussException("unknown "+node, node.getLine());
	}
	private Object eval(Scope scope, Expression expr) {
		final List<Object> values = new LinkedList<Object>();
		for (NodeInfo node: expr.getChildren()) {
			if (node instanceof Operator) {
				final int len = values.size();
				final Operator.Type opType = ((Operator)node).getType();
				final Object result, arg = values.remove(len - 1);
				switch (opType.getArgumentNumber()) {
				case 1:
					result = opType.invoke(arg);
					break;
				case 2:
					result = opType.invoke(values.remove(len - 2), arg);
					break;
				default:
					throw new UnsupportedOperationException(); 
				}
				values.add(result);
			} else if (node instanceof FunctionValue) {
				//TODO
			} else {
				values.add(eval(scope, node));
			}
		}
		return values.isEmpty() ? null: values.get(0);
	}
	private Object eval(Scope scope, VariableDefinition vdef) {
		return eval(scope, vdef.getExpression());
	}
	private Object eval(Scope scope, VariableValue vv) {
		return scope.get(vv.getName());
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

	private class Scope {
		private final Scope _parent;
		private final Map<String, VariableDefinition> _vars = new HashMap<String, VariableDefinition>();
		/** The value is an instanceof {@link FunctionDefinition} or {@link Method}.
		 */
		private final Map<String, Object> _funs = new HashMap<String, Object>();

		private Scope(Scope parent) {
			_parent = parent;
		}

		public Scope getParent() {
			return _parent;
		}
		public void put(VariableDefinition vdef) {
			final String nm = vdef.getName();
			for (Scope scope = this; scope != null; scope = scope._parent) {
				if (scope._vars.containsKey(nm)) {
					scope._vars.put(nm, vdef); //replace
					return;
				}
			}
			_vars.put(nm, vdef);
		}
		public void put(String name, Method mtd) {
			for (Scope scope = this; scope != null; scope = scope._parent) {
				if (scope._funs.containsKey(name)) {
					scope._funs.put(name, mtd); //replace
					return;
				}
			}
			_funs.put(name, vdef);
		}
		public void put(FunctionDefinition fdef) {
			final String nm = fdef.getName();
			for (Scope scope = this; scope != null; scope = scope._parent) {
				if (scope._funs.containsKey(nm)) {
					scope._funs.put(nm, fdef); //replace
					return;
				}
			}
			_funs.put(nm, vdef);
		}
		/** Returns the variable value with the given name. */
		public Object get(String name) {
			for (Scope scope = this; scope != null; scope = scope._parent) {
				final VariableDefinition vdef = scope._vars.get(name);
				if (vdef != null)
					return eval(scope, vdef);
			}

			if (_resolver != null) {
				final Object o = _resolver.getVariable(name);
				if (o != null)
					return o.toString();
			}
			return null;
		}
		/** Invokes a function. */
		public Object invoke(String name, Object... args) {
			return null;
		}
	}
}
