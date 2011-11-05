/* Tokenizer.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  3 12:44:00 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.in;

import java.util.Set;
import java.util.HashSet;
import java.io.Reader;
import java.io.IOException;

import static org.zkoss.zuss.impl.in.Parser.error;

/**
 * Tokenizer.
 * @author tomyeh
 */
/*package*/ class Tokenizer {
	private static final char EOF = (char)0;
	private static final String WHITESPACES = " \t\n\r";
	private static final String SYMBOLS = ":,{};";
	private static final String OPS = "()+-*/><=";

	private final Input _in;

	public Tokenizer(Reader in) {
		_in = new Input(in);
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
	/** Returns the next token, or null if EOF (end-of-file).
	 * @param expressioning whether the caller is expecting an expression,
	 * i.e., the operator is recognized.
	 */
	public Token next(boolean expressioning) throws IOException {
		char cc;
		do {
			cc = _in.next();
			if (cc == EOF) //no more
				return null; //EOF
		} while (WHITESPACES.indexOf(cc) >= 0);

		if (cc == '@')
			return asId();
		if (SYMBOLS.indexOf(cc) >= 0)
			return new Symbol(cc, getLine());
		if (expressioning && OPS.indexOf(cc) >= 0)
			return new Op(cc, getLine());
		return asOther(cc);
	}
	/** Peeks the nnext none-whitespace character.
	 * It won't change the state of this tokenizer.
	 * @return the next none-whitespace character.
	 */
	public char peek() throws IOException {
		char cc;
		do {
			cc = _in.next();
		} while (WHITESPACES.indexOf(cc) >= 0);
		if (cc != EOF)
			_in.putback(cc);
		return cc;
	}

	/** Returns the next token as {@link Selector} or {@link Other}.
	 */
	private Token asOther(char cc) throws IOException {
		final StringBuffer sb = new StringBuffer().append(cc);
		l_main:
		for (;;) {
			cc = _in.next();
			if (cc == EOF || cc == ';' || cc == '}' || cc == '@') {
				if (cc != EOF)
					_in.putback(cc);
				return new Other(sb.toString().trim(), getLine());

			} else if (cc == ',' || cc == '{') {
				_in.putback(cc);
				return new Selector(sb.toString().trim(), getLine());

			} else if (cc == ':' ) {
				//a colon might appear in a selector or a separator for name/value
				//	a:hover {background:blue;}
				//we consider it selector if it follows by , or {
				final StringBuffer ahead = new StringBuffer();
				for (;;) {
					cc = _in.next();
					if (cc == ',' || cc == '{') { //selector
						sb.append(':'); //colon is part of selector

						_in.putback(cc); //reverse order
						_in.putback(ahead);
						continue l_main; //keep processing char after colon

					} else if (cc == EOF || cc == ';' || cc == '}') { //separator
						if (cc != EOF)
							_in.putback(cc);
						_in.putback(ahead);
						_in.putback(':'); //colon is NOT part of selector
						return new Other(sb.toString().trim(), getLine());

					} else {
						ahead.append(cc);
					}
				}

			} else {
				sb.append(cc);
			}
		}
	}
	/** Returns the next token as ID.
	 */
	private Token asId() throws IOException {
		final StringBuffer sb = new StringBuffer();
		for (char cc; (cc = _in.next()) != EOF;) {
			if ((cc >= 'a' && cc <= 'z') || (cc >= 'A' && cc <= 'Z')
			|| cc == '-' || cc == '_' || (cc >= '0' && cc <= '9')) {
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
		return new Id(nm, getLine());
	}

	private static class Input {
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
			if (cc == '\n')
				--_lineno;
			_ahead.append(cc);
		}
		/** Put back what are read. */
		private void putback(CharSequence cs) {
			for (int j = cs.length(); --j >= 0;) //reverse order
				putback(cs.charAt(j));
		}
	}
}
