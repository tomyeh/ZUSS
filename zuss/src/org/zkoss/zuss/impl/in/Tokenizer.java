/* Tokenizer.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 12:44:00 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.in;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.Reader;
import java.io.IOException;

import org.zkoss.zuss.ZussException;
import org.zkoss.zuss.metainfo.Operator;

/**
 * Tokenizer.
 * @author tomyeh
 */
/*package*/ class Tokenizer {
	private static final char EOF = (char)0;
	private static final String WHITESPACES = " \t\n\r";
	private static final String SYMBOLS = ":,{};";
	private static final String OPS1 = "+-*/()";
	private static final Operator.Type[] OPTYPES1 = {
		Operator.Type.ADD, Operator.Type.SUBTRACT,
		Operator.Type.MULTIPLY, Operator.Type.DIVIDE,
		Operator.Type.LPAREN, Operator.Type.RPAREN
	};
	private static final String OPS2 = "><=!";
	private static final Operator.Type[] OPTYPES2 = {
		Operator.Type.GT, Operator.Type.LT,
		null, null
	};
	private static final Operator.Type[] OPTYPES2EQ = {
		Operator.Type.GE, Operator.Type.LE,
		Operator.Type.EQ, Operator.Type.NE
	};

	private Input _in;
	private String _filename;
	/** Read ahead (used to implement {@link #putback}. */
	private List<Token> _ahead;
	private final List<InputInfo> _inputs = new ArrayList<InputInfo>();

	public Tokenizer(Reader in, String filename) {
		init(in, filename);
	}
	private void init(Reader in, String filename) {
		_in = new Input(in);
		_filename = filename;
		_ahead = new ArrayList<Token>();
	}

	/** Read the tokens from the given input, and pushes the current input
	 * to a stack.
	 */
	public void pushInput(Reader in, String filename) {
		_inputs.add(0, new InputInfo(_in, _filename, _ahead));
		init(in, filename);
	}
	/** Give up the input pushed by {@link #pushInput}, and restores the previous
	 * input.
	 */
	public void popInput() {
		final InputInfo ii = _inputs.remove(0);
		_in = ii.input;
		_filename = ii.filename;
		_ahead = ii.ahead;
	}

	/** Returns the filename associated with this tokenizer.
	 */
	public String getFilename() {
		return _filename;
	}

	private ZussException error(String msg, int lineno) {
		return new ZussException(msg, getFilename(), lineno);
	}

	/** Returns the input.
	 */
	public Reader getInput() {
		return _in._in;
	}
	/** Returns the current line number.
	 */
	public int getLine() {
		return _in.getLine();
	}
	public void putback(Token token) {
		if (token != null)
			_ahead.add(0, token);
	}
	/** Returns the next token, or null if EOF (end-of-file).
	 * @param expressioning whether the caller is expecting an expression,
	 * i.e., the operator is recognized.
	 */
	public Token next(boolean expressioning) throws IOException {
		if (!_ahead.isEmpty())
			return _ahead.remove(0);

		char cc = skipWhitespaces();
		if (cc == EOF)
			return null;
		if (cc == '@')
			return asId();
		if (SYMBOLS.indexOf(cc) >= 0)
			return new Symbol(cc, getLine());

		if (expressioning) {
			if (cc == '\'' || cc == '"')
				return asString(cc);

			int j;
			if ((j = OPS1.indexOf(cc)) >= 0)
				return new Op(OPTYPES1[j], getLine());
			if ((j = OPS2.indexOf(cc)) >= 0) {
				final char c0 = _in.next();
				if (c0 == '=')
					return new Op(OPTYPES2EQ[j], getLine());

				final Operator.Type type = OPTYPES2[j];
				if (type == null)
					throw error("'=' expected after "+c0, getLine());
				_in.putback(c0);
				return new Op(type, getLine());
			}
			if (cc == '&' || cc == '|') {
				final char c0 = _in.next();
				if (c0 != cc)
					throw error("Unexpected "+c0, getLine());
				return new Op(cc == '&' ? Operator.Type.AND: Operator.Type.OR, getLine());
			}
		}
		return asOther(cc, expressioning);
	}
	private char skipWhitespaces() throws IOException {
		char cc;
		do {
			cc = _in.next();
		} while (cc != EOF && WHITESPACES.indexOf(cc) >= 0);
		return cc;
	}
	/** Returns the following content util (and including) the given character.
	 */
	public String getUntil(char upto) throws IOException {
		final StringBuffer sb = new StringBuffer();
		char quot = EOF;
		for (char cc = skipWhitespaces(); cc != EOF; cc = _in.next()) {
			sb.append(cc);
			if (cc == quot) {
				quot = EOF;
			} else if (cc == '\'' || cc == '"') {
				quot = cc;
			} else if (quot != EOF) {
				if (cc == '\\') {
					cc = _in.next();
					if (cc == EOF) break;
					sb.append(cc);
				}
			} else if (cc == upto) {
				break;
			}
		}
		return sb.toString();
	}
	/** Peeks the next none-whitespace character.
	 * It won't change the state of this tokenizer.
	 * @return the next none-whitespace character.
	 */
	public char peek() throws IOException {
		char cc = skipWhitespaces();
		_in.putback(cc);
		return cc;
	}
	/** Peeks the next non-whitespace character after the right parenthesis
	 * of a function or mixin.
	 * <p>Notice it shall be called right after LPAREN is returned.
	 */
	public char peekAfterRPAREN() throws IOException {
		final StringBuffer sb = new StringBuffer();
		int cparen = 1;
		char cc, quot = EOF;
		while ((cc = _in.next()) != EOF) {
			sb.append(cc);
			if (cc == quot) {
				quot = EOF;
			} else if (cc == '\'' || cc == '"') {
				quot = cc;
			} else if (quot != EOF) {
				if (cc == '\\') {
					cc = _in.next();
					if (cc == EOF) break;
					sb.append(cc);
				}
			} else if (cc == '(') {
				++cparen;
			} else if (cc == ')' && --cparen == 0) {
				cc = peek();
				break;
			}
		}
		_in.putback(sb);
		return cc;
	}

	/** Returns the next token as {@link Selector} or {@link Other}.
	 */
	private Token asOther(char cc, boolean expressioning) throws IOException {
		final StringBuffer sb = new StringBuffer().append(cc);
		final int lineno = getLine();
		if (expressioning)
			for (;;) {
				cc = _in.next();
				if (cc == EOF || WHITESPACES.indexOf(cc) >= 0
				|| SYMBOLS.indexOf(cc) >= 0
				|| OPS1.indexOf(cc) >= 0 || OPS2.indexOf(cc) >= 0) {
					_in.putback(cc);
					return new Other(sb.toString().trim(), lineno);
				}
				sb.append(cc);
			}

		//non-expression
		char quot = EOF;
		int strlineno = 0;
		for (;;) {
			cc = _in.next();
			if (cc == EOF)
				break;

			if (cc == quot) {
				quot = EOF;
			} else if (cc == '\'' || cc == '"') {
				quot = cc;
				strlineno = getLine();
			} else if (quot != EOF && cc == '\\') {
				sb.append(cc); //keep the original form since it is CSS selector (handled by browser)
				cc = _in.next();
				if (cc == EOF)
					break;
			}
			if (quot != EOF) {
				sb.append(cc);
				continue; //no special meaning
			}

			if (cc == ';' || cc == '}' || cc == '@') {
				_in.putback(cc);
				return new Other(sb.toString().trim(), lineno);

			} else if (cc == ',' || cc == '{') {
				_in.putback(cc);
				return new Selector(sb.toString().trim(), lineno);

			} else if (cc == ':' ) {
				//a colon might appear in a selector or a separator for name/value
				//	a:hover {background:blue;}
				//we consider it selector if it follows by , or {
				final StringBuffer ahead = new StringBuffer();
				for (;;) {
					cc = _in.next();
					if (cc == '{' || cc == ':') { //selector (',' is not a good separator)
						sb.append(':'); //colon is part of selector

						_in.putback(cc); //reverse order
						_in.putback(ahead);
						break; //keep processing char after colon

					} else if (cc == EOF || cc == ';' || cc == '}') { //separator
						_in.putback(cc);
						_in.putback(ahead);
						_in.putback(':'); //colon is NOT part of selector
						return new Other(sb.toString().trim(), getLine());

					} else {
						ahead.append(cc);
					}
				}
				continue;
			}
			sb.append(cc);
		}

		if (quot != EOF)
			throw error("unclosed string literal", strlineno);
		return new Other(sb.toString().trim(), lineno);
	}
	private static boolean isValidId(char cc) {
		return (cc >= 'a' && cc <= 'z') || (cc >= 'A' && cc <= 'Z')
			|| cc == '-' || cc == '_' || (cc >= '0' && cc <= '9');
	}
	/** Returns the next token as ID.
	 */
	private Token asId() throws IOException {
		final StringBuffer sb = new StringBuffer();
		for (char cc; (cc = _in.next()) != EOF;) {
			if (isValidId(cc)) {
				sb.append(cc);
			} else {
				_in.putback(cc);
				break;
			}
		}

		if (sb.length() == 0)
			throw error("identifier required after @", getLine());
		final String nm = sb.toString();
		if ("if".equals(nm))
			return new Keyword(Keyword.Value.IF, getLine());
		if ("else".equals(nm))
			return new Keyword(Keyword.Value.ELSE, getLine());
		if ("elif".equals(nm))
			return new Keyword(Keyword.Value.ELIF, getLine());
		if ("include".equals(nm))
			return new Keyword(Keyword.Value.INCLUDE, getLine());
		if ("import".equals(nm))
			return new Keyword(Keyword.Value.IMPORT, getLine());
		if ("media".equals(nm))
			return new Keyword(Keyword.Value.MEDIA, getLine());
		return new Id(nm, getLine());
	}
	private Token asString(char quot) throws IOException {
		final StringBuffer sb = new StringBuffer();
		final int lineno = getLine();
		for (char cc; (cc = _in.next()) != EOF;) {
			if (cc == quot)
				return new Other(sb.toString(), lineno);
			if (cc == '\\') {
				cc = _in.next(); //skip next
				if (cc == EOF)
					break;
			}
			sb.append(cc);
		}
		throw error("unclosed string literal", lineno);
	}

	private static class InputInfo {
		private final Input input;
		private final String filename;
		private final List<Token> ahead;
		private InputInfo(Input input, String filename, List<Token> ahead) {
			this.input = input;
			this.filename = filename;
			this.ahead = ahead;
		}
	}
	private class Input {
		private final Reader _in;
		private final char[] _buf = new char[4086];
		private final StringBuffer _ahead = new StringBuffer(); //read ahead
		private int _j, _len, _lineno = 1;

		private Input(Reader in) {
			_in = in;
		}

		/** Returns the line number. */
		private int getLine() {
			return _lineno;
		}
		/** Returns the next character. */
		private char next() throws IOException {
			char cc = _next();
			if (cc == '/' && skipComment())
				return next();
			return cc;
		}
		private char _next() throws IOException {
			char cc;
			if (_ahead.length() > 0) {
				cc = _ahead.charAt(_ahead.length() - 1);
				_ahead.deleteCharAt(_ahead.length() - 1);
			} else {
				while (_j >= _len) {
					if (_len < 0)
						return EOF;
					_len = _in.read(_buf, _j = 0, _buf.length);
				}
				cc = _buf[_j++];
			}

			if (cc == '\n')
				++_lineno;
			return cc;
		}
		private boolean skipComment() throws IOException {
			char cc = _next();
			if (cc != '*') {
				if (cc != EOF)
					putback(cc);
				return false; //not a comment
			}

			int lineno = getLine();
			while ((cc = _next()) != EOF) {
				if (cc == '*') {
					cc = _next();
					if (cc == '/')
						return true; //comment skipped
					if (cc == EOF)
						break; //failed
				}
			}
			throw error("unclosed comment", _lineno);
		}
		/** Put back what is read. It must be in stack order. */
		private void putback(char cc) {
			if (cc != EOF) {
				if (cc == '\n')
					--_lineno;
				_ahead.append(cc);
			}
		}
		/** Put back what are read. */
		private void putback(CharSequence cs) {
			for (int j = cs.length(); --j >= 0;) //reverse order
				putback(cs.charAt(j));
		}
	}
}
