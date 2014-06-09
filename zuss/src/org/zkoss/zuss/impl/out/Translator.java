/* Translator.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 12:10:15 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.out;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.zkoss.zuss.Resolver;
import org.zkoss.zuss.ZussException;
import org.zkoss.zuss.metainfo.ArgumentDefinition;
import org.zkoss.zuss.metainfo.BlockDefinition;
import org.zkoss.zuss.metainfo.ConstantValue;
import org.zkoss.zuss.metainfo.Expression;
import org.zkoss.zuss.metainfo.FunctionDefinition;
import org.zkoss.zuss.metainfo.FunctionValue;
import org.zkoss.zuss.metainfo.IfDefinition;
import org.zkoss.zuss.metainfo.MediaDefinition;
import org.zkoss.zuss.metainfo.MixinDefinition;
import org.zkoss.zuss.metainfo.NodeInfo;
import org.zkoss.zuss.metainfo.Operator;
import org.zkoss.zuss.metainfo.RawValue;
import org.zkoss.zuss.metainfo.RuleDefinition;
import org.zkoss.zuss.metainfo.StyleDefinition;
import org.zkoss.zuss.metainfo.VariableDefinition;
import org.zkoss.zuss.metainfo.ZussDefinition;
import org.zkoss.zuss.util.Classes;

/**
 * The translator used to translate ZUSS to CSS.
 * @author tomyeh
 */
public class Translator {
	private static final Object NULL = new Object() {
		@Override
		public String toString() {
			return "";
		}
	};

	private final ZussDefinition _zuss;
	private final Writer _out;
	private final Resolver _resolver;
	private final Resolver _builtin = new BuiltinResolver();

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
			outChildren(scope, null, _zuss);
		} finally {
			try {
				_out.close();
			} catch (Throwable t) {
			}
		}
	}

	private ZussException error(String msg, NodeInfo node) {
		return new ZussException(msg, _zuss.getFilename(), node.getLine());
	}
	private ZussException error(String msg, int lineno) {
		return new ZussException(msg, _zuss.getFilename(), lineno);
	}
	private ZussException error(String msg, int lineno, Throwable t) {
		return new ZussException(msg, _zuss.getFilename(), lineno, t);
	}

	private void outChildren(Scope scope, List<String> outerSels, NodeInfo node)
	throws IOException {
		for (NodeInfo child: node.getChildren()) {
            outNode(scope, outerSels, child);
        }
	}
	private void outNode(Scope scope, List<String> outerSels, NodeInfo node)
	throws IOException {
		if (node instanceof RuleDefinition) {
			outRule(scope, outerSels, (RuleDefinition)node);
				//Like JavaScript, a rule doesn't instantiate a new scope.
				//Otherwise, it is tough to implement the local scope
		} else if (node instanceof Expression) {
		//here must be a mixin, since outChildren is called at the same level
		//as rules (such as at top-level or in @media)
			final MixinInfo mixin = getMixinInfo(scope, (Expression)node);
			if (mixin == null) {
                throw error("only a mixin invocation is allowed", node);
            }

			final Scope subsc = new LocalScope(scope, mixin.argmap, mixin.argmap.values());
			for (NodeInfo subnd: mixin.mixin.getChildren()) {
                outRuleInner(subsc, outerSels, subnd, "", "", true);
            }
		} else {
			outOther(scope, outerSels, node);
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
			for (String s: rdef.getSelectors()) {
                for (String os: outerSels) {
                    thisSels.add(s.startsWith("&") ? os + s.substring(1): os + ' ' + s);
                }
            }
		}

		final String head = cat(thisSels) + "{\n", end = "}\n";
		boolean empty = true;

		for (NodeInfo node: rdef.getChildren()) {
            empty = outRuleInner(scope, thisSels, node, head, end, empty);
        }

		if (!empty) {
            write(end);
        }
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
		} else if (node instanceof Expression) { //could be mixin or value expression
			final Expression expr = (Expression)node;
			final MixinInfo mixin = getMixinInfo(scope, expr);
			if (mixin != null) { //yes, a minin
				if (empty) {
					empty = false;
					write(head);
				}

				final Scope subsc = new LocalScope(scope, mixin.argmap, mixin.argmap.values());
				for (NodeInfo subnd: mixin.mixin.getChildren()) {
					empty = outRuleInner(subsc, thisSels, subnd, head, end, empty);
				}
			} else {
			//handle normal expression
				final Object o = evalExpression(scope, expr);
				if (o != null) {
					if (empty) {
						empty = false;
						write(head);
					}
					write(Classes.toString(o));
				}
			}
		} else if (node instanceof IfDefinition) { //in a rule
			for (NodeInfo child: node.getChildren()) {
				final BlockDefinition block = (BlockDefinition)child;
				final Expression expr = block.getCondition();
				if (expr == null || isTrue(scope, expr)) {
					for (NodeInfo subnd: block.getChildren()) {
						empty = outRuleInner(scope, thisSels, subnd, head, end, empty);
					}
					break; //done
				}
			}
		} else {
			outOther(scope, thisSels, node);
		}
		return empty;
	}

	/** Returns the map of arguments, or null if expr is not a mixin.
	 */
	private MixinInfo getMixinInfo(Scope scope, Expression expr)
	throws IOException {
		final List<NodeInfo> exprList = expr.getChildren();
		final int j;
		final FunctionValue fv;
		Object fn;
		if ((j = exprList.size() - 1) >= 0
		&& ((fn=exprList.get(j)) instanceof FunctionValue)
		&& (fn = scope.getVariable((fv=(FunctionValue)fn).getName(), false))
			instanceof MixinDefinition) {
			List<Object> values = evalExpression(scope, exprList.subList(0, j));
			return new MixinInfo((MixinDefinition)fn,
				 getArgumentMap(
					((MixinDefinition)fn).getArgumentDefinitions(),
					getArguments(values, fv.getArgumentNumber(), fv.getLine())));
		}
		return null;
	}
	private static class MixinInfo {
		private final MixinDefinition mixin;
		private final Map<String, Object> argmap;
		private MixinInfo(MixinDefinition mixin, Map<String, Object> argmap) {
			this.mixin = mixin;
			this.argmap = argmap;
		}
	}

	private void outStyle(Scope scope, StyleDefinition sdef) throws IOException {
		write('\t');
		write(sdef.getName());
		write(':');

		for (NodeInfo node: sdef.getChildren()) {
			final Object value = evalNode(scope, node);
			if (value != null) {
				write(' ');
				write(Classes.toString(value));
			}
		}

		write(";\n");
	}

	/** Generates definitions other than rules, styles and mixins. */
	private void outOther(Scope scope, List<String> outerSels, NodeInfo node) throws IOException {
		if (node instanceof VariableDefinition) {
			final VariableDefinition vdef = (VariableDefinition)node;
			scope.setVariable(vdef.getName(), evalDefinition(scope, vdef));
				//spec: evaluate when it is defined (not when it is used)
		} else if (node instanceof FunctionDefinition) {
			final FunctionDefinition fd = (FunctionDefinition)node;
			scope.setVariable(fd.getName(), fd);
		} else if (node instanceof MixinDefinition) {
			final MixinDefinition fd = (MixinDefinition)node;
			scope.setVariable(fd.getName(), fd);
		} else if (node instanceof IfDefinition) { //assume not in a rule (outerSel shall be null)
			for (NodeInfo child: node.getChildren()) {
				final BlockDefinition block = (BlockDefinition)child;
				final Expression expr = block.getCondition();
				if (expr == null || isTrue(scope, expr)) {
					outChildren(scope, outerSels, block);
					break; //done
				}
			}
		} else if (node instanceof MediaDefinition) {
			write("@media ");
			write(((MediaDefinition)node).getRange());
			write("{\n");
			outChildren(scope, outerSels, node);
			write ("}\n");
		} else if (node instanceof RawValue) {
			write(((RawValue)node).getValue());
		} else {
			throw error("unknown "+node, node);
		}
	}

	private Object evalNode(Scope scope, NodeInfo node) {
		if (node instanceof ConstantValue) {
            return ((ConstantValue)node).getValue();
        }
		if (node instanceof FunctionValue) {
            return evalFunctionValue(scope, (FunctionValue)node, new Object[0]);
        }
		if (node instanceof Expression)
         {
            return evalExpression(scope, (Expression)node); //must be value expression, not mixin
        }
		throw error("unknown "+node, node);
	}
	/** @param expr it must be a value expression, not mixin. */
	private Object evalExpression(Scope scope, Expression expr) {
		final List<Object> values = evalExpression(scope, expr.getChildren());
		if (values.size() != 1) {
            throw error("failed evaluate "+expr+": "+values, expr);
        }
		return values.get(0);
	}
	private boolean isTrue(Scope scope, Expression expr) {
		final Boolean b = (Boolean)Classes.coerce(Boolean.class,
			evalExpression(scope, expr));
		return b != null && b.booleanValue();
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
				values.add(evalFunctionValue(scope, fv,
					getArguments(values, fv.getArgumentNumber(), fv.getLine())));
			} else {
				values.add(evalNode(scope, node));
			}
		}
		return values;
	}
	private Object evalFunctionValue(Scope scope, FunctionValue fv, Object[] args) {
		final Object o = scope.getVariable(fv.getName(), true); //NULL is expected
		final int lineno = fv.getLine();
		if (o instanceof MixinDefinition) {
            throw error("Mixin not allowed, "+o, lineno);
        }
		if (o instanceof FunctionDefinition) {
            return evalDefinition(scope, (FunctionDefinition)o, args, lineno);
        }
		if (o != null) {
            return o != NULL ? o: null;
        }

		//check if it is a method provided by the resolver
		final String name = fv.getName();
		final Callable<Object> mtd = getMethod(name, args);
		if (mtd == null) {
			if (fv.isVariableLook())
             {
                return null; //consider as null
            }
			throw error("Function not found: "+name, lineno);
		}
		try {
			return mtd.call();
		} catch (Exception ex) {
			throw error("Unable to invoke "+mtd, lineno, ex);
		}
	}
	private Callable<Object> getMethod(String name, Object[] args) {
		if (_resolver != null) {
			final Callable<Object> mtd = _resolver.getMethod(name, args);
			if (mtd != null) {
                            return mtd;
                        }
		}
		return _builtin.getMethod(name, args);
	}
	private Object evalDefinition(Scope scope, VariableDefinition vdef) {
		return evalExpression(scope, vdef.getExpression());
	}
	private Object evalDefinition(Scope scope, FunctionDefinition fdef, Object[] args,
	int lineno) {
		final ArgumentDefinition[] adefs = fdef.getArgumentDefinitions();
		final Object value;
		final Expression expr = fdef.getExpression();
		if (expr != null) {
			final Map<String, Object> argmap = getArgumentMap(adefs, args);
			value = evalExpression(new LocalScope(scope, argmap, argmap.values()), expr);
				//a local scope for function invocation
		} else {
			final Method m = fdef.getMethod();
			final Class<?>[] argTypes = m.getParameterTypes();
			final Object[] as = args;
			if (args.length != argTypes.length) {
                args = new Object[argTypes.length];
            }
			for (int j = 0; j < argTypes.length; ++j) {
                args[j] = Classes.coerce(argTypes[j],
					j < as.length ? as[j]: adefs[j].getDefaultValue());
            }
			try {
				value = m.invoke(null, args);
			} catch (Throwable ex) {
				throw error("failed to invoke "+m, lineno, ex);
			}
		}
		return value;
	}
	private static Map<String, Object>
	getArgumentMap(ArgumentDefinition[] adefs, Object[] args) {
		final Map<String, Object> argmap = new LinkedHashMap<String, Object>(); //preserve order
		for (int j = 0; j < adefs.length; ++j) {
			argmap.put(adefs[j].getName(),
				j < args.length ? args[j]: adefs[j].getDefaultValue());
		}
		return argmap;
	}
	private Object[] getArguments(List<Object> values, int argc, int lineno) {
		int sz = values.size();
		if (sz < argc) {
            throw error("Not enough argument: "+sz, lineno);
        }
		final Object[] args = new Object[argc];
		while ( --argc >= 0) {
            args[argc] = values.remove(--sz);
        }
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
			if (sb.length() > 0) {
                sb.append(',');
            }
			sb.append(s);
		}
		return sb.toString();
	}

	private class Scope {
		private final Scope _parent;
		private final Map<String, Object> _vars = new HashMap<String, Object>();

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

		/** Returns the variable value with the given name.
		 * Notice: the returned value could be FunctionDefinition or MixinDefinition.
		 *
		 * @param nullAware if true and the value is null, {@link #NULL} is returned.
		 */
		public Object getVariable(String name, boolean nullAware) {
			for (Scope scope = this; scope != null; scope = scope._parent) {
				final Object o = scope._vars.get(name);
				if (o != null || scope._vars.containsKey(name)) {
                    return o != null || !nullAware ? o: NULL;
                }
			}

			if (_resolver != null) {
				final Object o = _resolver.getVariable(name);
				if (o != null) {
                    return o;
                }
			}
			return _builtin.getVariable(name);
		}
		@Override
		public String toString() {
			return "scope("+_parent+')';
		}
	}
	/** Represents a local scope used by the evaluation of mixin and function.
	 */
	private class LocalScope extends Scope {
		private final Collection _args;
		private LocalScope(Scope parent, Map<String, Object> vars, Collection args) {
			super(parent instanceof LocalScope ? parent._parent: parent, vars);
				//LocalScope's parent can not be another LocalScope
			_args = args;
		}
		@Override
		public Object getVariable(String name, boolean nullAware) {
			final Object o = super.getVariable(name, nullAware);
			if (o != null) {
                return o;
            }
			return "arguments".equals(name) ? _args: null;
		}
	}
}
