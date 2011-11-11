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
import org.zkoss.zuss.ZussException;
import org.zkoss.zuss.util.Classes;
import org.zkoss.zuss.metainfo.NodeInfo;
import org.zkoss.zuss.metainfo.ZussDefinition;
import org.zkoss.zuss.metainfo.RuleDefinition;
import org.zkoss.zuss.metainfo.StyleDefinition;
import org.zkoss.zuss.metainfo.VariableDefinition;
import org.zkoss.zuss.metainfo.FunctionDefinition;
import org.zkoss.zuss.metainfo.MixinDefinition;
import org.zkoss.zuss.metainfo.ArgumentDefinition;
import org.zkoss.zuss.metainfo.Expression;
import org.zkoss.zuss.metainfo.FunctionValue;
import org.zkoss.zuss.metainfo.ConstantValue;
import org.zkoss.zuss.metainfo.VariableValue;
import org.zkoss.zuss.metainfo.Operator;
import static org.zkoss.zuss.metainfo.Operator.Type.*;

/**
 * The translator used to translate ZUSS to CSS.
 * @author tomyeh
 */
public class Translator {
	private final ZussDefinition _zuss;
	private final Writer _out;
	private final Resolver _resolver;

	public Translator(ZussDefinition zuss, Writer out, Resolver resolver) {
		_zuss = zuss;
		_out = out;
		_resolver = resolver;
	}
	/** Generates the CSS content.
	 * <p>Notice that this method can be called only once.
	 */
	public void translate() throws IOException {
		try {
			final Scope scope = new Scope(null);
			for (NodeInfo node: _zuss.getChildren()) {
				if (node instanceof RuleDefinition) {
					outRule(scope, null, (RuleDefinition)node);
						//Like JavaScript, a rule doesn't instantiate a new scope.
						//Otherwise, it is tough to implement the local scope
				} else {
					outOther(scope, node);
				}
			}
		} finally {
			try {
				_out.close();
			} catch (Throwable t) {
			}
		}
	}
	/** Generates the rule definitions.
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

		for (NodeInfo node: rdef.getChildren())
			empty = outRuleInner(scope, thisSels, node, head, end, empty);

		if (!empty)
			write(end);
	}
	private boolean outRuleInner(Scope scope, List<String> thisSels,
	NodeInfo node, String head, String end, boolean empty)
	throws IOException {
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
		} else if (node instanceof Expression) {
			final Expression expr = (Expression)node;
			final List<NodeInfo> exprList = expr.getChildren();
			final int j;
			final NodeInfo lastChild;
			final FunctionValue fv;
			final NodeInfo fn;
			if ((j = exprList.size() - 1) >= 0
			&& ((lastChild=exprList.get(j)) instanceof FunctionValue)
			&& (fn = scope.getFunction((fv=(FunctionValue)lastChild).getName())) instanceof MixinDefinition) {
			//handle mixin
				if (empty) {
					empty = false;
					write(head);
				}

				List<Object> values = evalExpression(scope, exprList.subList(0, j));
				Map<String, Object> argmap = getArgumentMap(
					((MixinDefinition)fn).getArgumentDefinitions(),
					getArguments(values, fv.getArgumentNumber(), fv.getLine()));
				final Scope subsc = new LocalScope(scope, argmap); //a local scope
				for (NodeInfo subnd: fn.getChildren()) {
					empty = outRuleInner(subsc, thisSels, subnd, head, end, empty);
				}
			} else {
			//handle normal expression
				final Object o = eval(scope, expr);
				if (o != null) {
					if (empty) {
						empty = false;
						write(head);
					}
					write(o.toString());
				}
			}
		} else {
			outOther(scope, node);
		}
		return empty;
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

	/** Generates definitions other than rules and styles. */
	private void outOther(Scope scope, NodeInfo node) throws IOException {
		if (node instanceof VariableDefinition) {
			final VariableDefinition vdef = (VariableDefinition)node;
			scope.setVariable(vdef.getName(), eval(scope, vdef));
				//spec: evaluate when it is defined (not when it is used)
		} else if (node instanceof FunctionDefinition) {
			scope.setFunction((FunctionDefinition)node);
		} else if (node instanceof MixinDefinition) {
			scope.setFunction((MixinDefinition)node);
		} else {
			throw new ZussException("unknown "+node, node.getLine());
		}
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
		final List<Object> values = evalExpression(scope, expr.getChildren());
		if (values.size() != 1)
			throw new ZussException("failed evaluate "+expr+": "+values, expr.getLine());
		return values.get(0);
	}
	private List<Object> evalExpression(Scope scope, List<NodeInfo> exprList) {
		final List<Object> values = new LinkedList<Object>();
		for (NodeInfo node: exprList) {
			if (node instanceof Operator) {
				final Operator.Type opType = ((Operator)node).getType();
				values.add(opType.invoke(getArguments(
					values, opType.getArgumentNumber(), node.getLine())));
			} else if (node instanceof FunctionValue) {
				final FunctionValue fv = (FunctionValue)node;
				final NodeInfo fn = scope.getFunction(fv.getName());
				final int lineno = fv.getLine();
				final Object[] args = getArguments(values, fv.getArgumentNumber(), lineno);
				if (fn == EVAL_FUNC) {
					values.add(args.length == 0 ? null: args[0]);
				} else if (fn instanceof FunctionDefinition) {
					values.add(eval(scope, (FunctionDefinition)fn, args, lineno));
				} else if (fn instanceof MixinDefinition) {
					throw new ZussException("not allowed, "+fn, lineno);
				} else {
					values.add(invoke(fv.getName(), args, lineno));
				}
			} else {
				values.add(eval(scope, node));
			}
		}
		return values;
	}
	/** Invokes a function. */
	public Object invoke(String name, Object[] args, int lineno) {
		if (_resolver != null) {
			final Method mtd = _resolver.getMethod(name);
			if (mtd != null) {
				int j = mtd.getParameterTypes().length;
				if (args.length < j) { //if not enough, all others assume null
					final Object[] as = args;
					args = new Object[j];
					for (j = as.length; --j >= 0;)
						args[j] = as[j];
				}
				try {
					return mtd.invoke(args);
				} catch (Exception ex) {
					throw new ZussException("Unable to invoke "+mtd, lineno, ex);
				}
			}
		}
		throw new ZussException("Function not found: "+name, lineno);
	}
	private Object eval(Scope scope, VariableDefinition vdef) {
		return eval(scope, vdef.getExpression());
	}
	private Object eval(Scope scope, FunctionDefinition fdef, Object[] args,
	int lineno) {
		final ArgumentDefinition[] adefs = fdef.getArgumentDefinitions();
		final Object value;
		final Expression expr = fdef.getExpression();
		if (expr != null) {
			value = eval(new LocalScope(scope, getArgumentMap(adefs, args)), expr);
				//a local scope for function invocation
		} else {
			final Method m = fdef.getMethod();
			final Class<?>[] argTypes = m.getParameterTypes();
			final Object[] as = args;
			if (args.length != argTypes.length)
				args = new Object[argTypes.length];
			for (int j = 0; j < argTypes.length; ++j)
				args[j] = Classes.coerce(argTypes[j],
					j < as.length ? as[j]: adefs[j].getDefaultValue());
			try {
				value = m.invoke(null, args);
			} catch (Throwable ex) {
				throw new ZussException("failed to invoke "+m, lineno, ex);
			}
		}
		return value;
	}
	private static Map<String, Object>
	getArgumentMap(ArgumentDefinition[] adefs, Object[] args) {
		final Map<String, Object> argmap = new HashMap<String, Object>();
		for (int j = 0; j < adefs.length; ++j) {
			argmap.put(adefs[j].getName(),
				j < args.length ? args[j]: adefs[j].getDefaultValue());
		}
		return argmap;
	}
	private Object eval(Scope scope, VariableValue vv) {
		return scope.getVariable(vv.getName());
	}
	private Object[] getArguments(List<Object> values, int argc, int lineno) {
		int sz = values.size();
		if (sz < argc)
			throw new ZussException("Not enough argument: "+sz, lineno);
		final Object[] args = new Object[argc];
		while ( --argc >= 0)
			args[argc] = values.remove(--sz);
		return args;
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
		private final Map<String, Object> _vars = new HashMap<String, Object>();
		/** The value is an instanceof {@link FunctionDefinition} or {@link MuxinDefinition}.
		 */
		private final Map<String, NodeInfo> _funs = new HashMap<String, NodeInfo>();

		private Scope(Scope parent) {
			_parent = parent;
		}
		/**
		 * @param vars the initial variables
		 */
		private Scope(Scope parent, Map<String, Object> vars) {
			_parent = parent;
			_vars.putAll(vars);
		}

		public Scope getParent() {
			return _parent;
		}

		/** Stores a variable. */
		public void setVariable(String name, Object value) {
			for (Scope scope = this; scope != null; scope = scope._parent) {
				if (scope._vars.containsKey(name)) {
					scope._vars.put(name, value); //replace
					return;
				}
			}
			_vars.put(name, value);
		}
		/** Stores a function definition. */
		public void setFunction(FunctionDefinition fdef) {
			setFunction(fdef.getName(), fdef);
		}
		public void setFunction(MixinDefinition mdef) {
			setFunction(mdef.getName(), mdef);
		}
		private void setFunction(String name, NodeInfo node) {
			for (Scope scope = this; scope != null; scope = scope._parent) {
				if (scope._funs.containsKey(name)) {
					scope._funs.put(name, node); //replace
					return;
				}
			}
			_funs.put(name, node);
		}

		/** Returns the variable value with the given name. */
		public Object getVariable(String name) {
			for (Scope scope = this; scope != null; scope = scope._parent) {
				final Object o = scope._vars.get(name);
				if (o != null || scope._vars.containsKey(name))
					return o;
			}
			return _resolver != null ? _resolver.getVariable(name): null;
		}
		/** Returns {@link FunctionDefinition} or {@link MixinDefinition}
		 * of the given name, or null if not exists.
		 */
		public NodeInfo getFunction(String name) {
			for (Scope scope = this; scope != null; scope = scope._parent) {
				final NodeInfo node = scope._funs.get(name);
				if (node != null)
					return node;
			}
			if ("eval".equals(name))
				return EVAL_FUNC; //built-in function
			return null;
		}
	}
	/** Represents a local scope used by the evaluation of mixin and function.
	 */
	private class LocalScope extends Scope {
		private LocalScope(Scope parent, Map<String, Object> vars) {
			super(parent instanceof LocalScope ? parent._parent: parent, vars);
				//LocalScope's parent can not be another LocalScope
		}
	}

	//builtin functions//
	private final static FunctionDefinition EVAL_FUNC =
		new FunctionDefinition(null, "eval", new ArgumentDefinition[0], (Expression)null, 0);
}
