/* Operator.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov  7 12:08:21 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

/**
 * Represents an operator.
 *
 * @author tomyeh
 */
public class Operator extends LeafInfo {
	private Type _type;
	public Operator(NodeInfo parent, Type type, int lineno) {
		super(parent, lineno);
		_type = type;
	}

	/** Returns the type of the operator.
	 */
	public Type getType() {
		return _type;
	}

	@Override
	public String toString() {
		return "" + _type;
	}

	/** Types of the operators. */
	public static enum Type {
		//Follow Java's precedence: http://en.wikipedia.org/wiki/Order_of_operations
		/** The negative sign. */
		MINUS( "-", 2),
		ADD("+", 4), SUBTRACT("-", 4), MULTIPLY("*", 3), DIVIDE("/", 3),
		EQ("==", 7), NE("!=", 7), OR("||", 12), AND("&&", 11),
		GT(">", 6), LT("<", 6), GE(">=", 6), LE("<=", 6),
		/** Left parenthesis, '('. Not a real operator but for parsing purpose. */
		LPAREN("(", 99), //99 so it won't be popped up until )
		/** Right parenthesis, ')'. Not a real operator but for parsing purpose. */
		RPAREN(")", 99),
		/** Function operator. Not a real operator but for parsing purpose. */
		FUNC("functin(", 99), //99 so it won't be popped up until )
		/** Argument separator. Not a real operator but for parsing purpose. */
		COMMA(",", 99); //99 so it won't be popped up until )

		private final int _precedence;
		private final String _name;

		private Type(String name, int precedence) {
			_name = name;
			_precedence = precedence;
		}

		/** Returns the precedence of this operator.
		 * The lower the number, the higher the precedence.
		 */
		public int getPrecedence() {
			return _precedence;
		}
		/** Returns the name of this operator.
		 */
		public String getName() {
			return _name;
		}

		@Override
		public String toString() {
			return '\'' + _name + '\'';
		}
	}
}
