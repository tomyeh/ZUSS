/* Parser.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 11:35:27 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.in;

import java.util.List;
import java.util.LinkedList;
import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;

import org.zkoss.zuss.Locator;
import org.zkoss.zuss.ZussException;
import org.zkoss.zuss.metainfo.NodeInfo;
import org.zkoss.zuss.metainfo.SheetDefinition;
import org.zkoss.zuss.metainfo.RuleDefinition;
import org.zkoss.zuss.metainfo.StyleDefinition;
import org.zkoss.zuss.metainfo.ValueDefinition;
import org.zkoss.zuss.metainfo.VariableInvocation;

/**
 * The ZUSS parser.
 * @author tomyeh
 */
public class Parser {
	private final Tokenizer _in;
	private final Locator _loc;
	/** Read ahead (used to implement {@link #putback}. */
	private Token _ahead;

	public Parser(Reader in, Locator loc) {
		_in = new Tokenizer(in);
		_loc = loc;
	}

	/** Parses the ZUSS style sheet.
	 * <p>Notice that this method can be called only once.
	 */
	public SheetDefinition parse() throws IOException {
		try {
			Context ctx = new Context();
			parse(ctx);
			return ctx.sheet;
		} finally {
			try {
				_in.getInput().close();
			} catch (Throwable t) {
			}
		}
	}
	private void parse(Context ctx) throws IOException {
		for (Token token; (token = next(ctx)) != null;) {
System.out.println(">"+D.d(token) + " for " +ctx.state.parent);
			if (token instanceof Keyword) {
				parseKeyword(ctx, (Keyword)token);
			} else if (token instanceof Id) {
				parseId(ctx, (Id)token);
			} else if (token instanceof Selector) {
				final RuleDefinition rdef = new RuleDefinition(ctx.state.parent, token.getLine());
				rdef.getSelectors().add(((Selector)token).getValue());
				parseSelector(ctx, rdef);
			} else if (ctx.state.bracing && token instanceof Symbol
			&& ((Symbol)token).getValue() == '}') {
				return; //done (closed)
			} else if (token instanceof Other) {
				if (ctx.state.parent instanceof SheetDefinition)
					throw error("{ expected", token);
				parseStyle(ctx, (Other)token);
			} else {
				throw error("unknown token "+token, token);
			}
		}
	}
	private void parseKeyword(Context ctx, Keyword kw) throws IOException {
	}
	private void parseId(Context ctx, Id id) throws IOException {
	}
	private void parseSelector(Context ctx, RuleDefinition rdef) throws IOException {
		Token t0 = next(ctx);
System.out.println(">"+D.d(t0)+" for "+rdef.getSelectors());
		char symbol;
		if (!(t0 instanceof Symbol)
		|| ((symbol = ((Symbol)t0).getValue()) != ',' && symbol != '{'))
			throw error(", or { expected after a selector", _in.getLine());

		if (symbol == ',') {
			Token t1 = next(ctx);
			if (!(t1 instanceof Selector))
				throw error("a selector expected after ','", t0);
			rdef.getSelectors().add(((Selector)t1).getValue());
			parseSelector(ctx, rdef);
		} else { //{
			ctx.push(new State(rdef, true));
			parse(ctx);
			ctx.pop();
		}
	}
	private void parseStyle(Context ctx, Other name) throws IOException {
		Token t0 = next(ctx);
		if (!(t0 instanceof Symbol) || ((Symbol)t0).getValue() != ':')
			throw error(": expected", t0 != null ? t0: name);

		StyleDefinition sdef = new StyleDefinition(ctx.state.parent, name.getValue(), name.getLine());
		for (Token token; (token = next(ctx)) != null;) {
			if (token instanceof Other) {
System.out.println(">"+D.d(token)+" for "+sdef);
				new ValueDefinition(sdef, ((Other)token).getValue(), token.getLine());
			} else if (token instanceof Symbol) {
				final char symbol = ((Symbol)token).getValue();
				if (symbol == ';')
					break; //done
				if (symbol == '}') {
					putback(token);
					break; //done
				}
				throw error("unexpected '" + symbol + '\'', symbol);
			} else if (token instanceof Id) {
				//handle @xx or @xxx()
				if (_in.peek() == '(') { //a function invocation
					//TODO
				} else {
System.out.println(">"+D.d(token)+" for "+sdef);
					new VariableInvocation(sdef, ((Id)token).getValue(), token.getLine());
				}
			}
		}
	}

	private void putback(Token token) throws IOException {
		if (_ahead != null)
			throw new InternalError("Failed to put back "+token);
		_ahead = token;
	}
	private Token next(Context ctx) throws IOException {
		if (_ahead != null) {
			Token token = _ahead;
			_ahead = null;
			return token;
		}
		return _in.next(ctx.state.expressioning);
	}
	/*package*/ static ZussException error(String msg, int lineno) {
		return new ZussException("Line " + lineno + ": " + msg);
	}
	private static final ZussException error(String msg, Token token) {
		return error(msg, token.getLine());
	}
	private final ZussException eof() {
		return error("unexpected end-of-file", _in.getLine());
	}

	private class Context {
		private final SheetDefinition sheet = new SheetDefinition();
		private final List<State> _states = new LinkedList<State>();
		private State state = new State(sheet, false);

		private void push(State state) {
			_states.add(0, this.state);
			this.state = state;
		}
		private void pop() {
			this.state = _states.remove(0);
		}
	}
	private class State {
		/** The parent node. */
		private final NodeInfo parent;
		/** whether { is encountered, but not }. That is, } is expected. */
		private final boolean bracing;
		/** whether it is parsing an expression, i.e., operations are recognized. */
		private boolean expressioning;

		private State(NodeInfo parent, boolean bracing) {
			this.parent = parent;
			this.bracing = bracing;
		}
	}
}
