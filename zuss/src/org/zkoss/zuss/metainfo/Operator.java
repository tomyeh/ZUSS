/* Operator.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov  7 12:08:21 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.metainfo;

import org.zkoss.zuss.util.Operators;

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
		/** Concatenation. Represent the 'implied' operator if two consecutive
		 * operands have no opearator in between.
		 */
		CONCAT(" ", 0) { //highest precedence
			@Override
			public Object invoke(Object... args) {
				Object v1 = args[0], v2 = args[1];
				return v1 != null ? v2 != null ? v1.toString() + ' ' + v2.toString():
					v1: v2;
			}
		},
		/** The negation. */
		NEGATE("-", 1, 2) { //unary
			@Override
			public Object invoke(Object... args) {
				return Operators.negate(args[0]);
			}
		},
		/** The addition. */
		ADD("+", 4) {
			@Override
			public Object invoke(Object... args) {
				return Operators.add(args[0], args[1]);
			}
		},
		/** The subtraction. */
		SUBTRACT("-", 4) {
			@Override
			public Object invoke(Object... args) {
				return Operators.subtract(args[0], args[1]);
			}
		},
		/** The multiplication. */
		MULTIPLY("*", 3) {
			@Override
			public Object invoke(Object... args) {
				return Operators.multiply(args[0], args[1]);
			}
		},
		/** The division. */
		DIVIDE("/", 3) {
			@Override
			public Object invoke(Object... args) {
				return Operators.divide(args[0], args[1]);
			}
		},
		/** Equals. */
		EQ("==", 7) {
			@Override
			public Object invoke(Object... args) {
				return Operators.equals(args[0], args[1]);
			}
		},
		/** Not equals. */
		NE("!=", 7) {
			@Override
			public Object invoke(Object... args) {
				return !Operators.equals(args[0], args[1]);
			}
		},
		/** The OR oeprator. */
		OR("||", 12) {
			@Override
			public Object invoke(Object... args) {
				return Operators.isTrue(args[0]) || Operators.isTrue(args[1]);
			}
		},
		/** The AND operatr. */
		AND("&&", 11) {
			@Override
			public Object invoke(Object... args) {
				return Operators.isTrue(args[0]) && Operators.isTrue(args[1]);
			}
		},
		/** Greater than. */
		GT(">", 6) {
			@Override
			public Object invoke(Object... args) {
				return Operators.compare(args[0], args[1]) > 0;
			}
		},
		/** Less than. */
		LT("<", 6) {
			@Override
			public Object invoke(Object... args) {
				return Operators.compare(args[0], args[1]) < 0;
			}
		},
		/** Greater than or equals. */
		GE(">=", 6) {
			@Override
			public Object invoke(Object... args) {
				return Operators.compare(args[0], args[1]) >= 0;
			}
		},
		/** Less than or equals. */
		LE("<=", 6) {
			@Override
			public Object invoke(Object... args) {
				return Operators.compare(args[0], args[1]) <= 0;
			}
		},
		/** Left parenthesis, '('. Not a real operator but for parsing purpose. */
		LPAREN("(", 99), //99 so it won't be popped up until )
		/** Right parenthesis, ')'. Not a real operator but for parsing purpose. */
		RPAREN(")", 99),
		/** Function operator. Not a real operator but for parsing purpose. */
		FUNC("functin(", 99), //99 so it won't be popped up until )
		/** Argument separator. Not a real operator but for parsing purpose. */
		COMMA(",", 99); //99 so it won't be popped up until )

		private final String _name;
		private final int _argc;
		private final int _precedence;

		/**
		 * @param precedence the precedence. The lower, the higher priority.
		 * @param argc the number of arguments
		 */
		private Type(String name, int argc, int precedence) {
			_name = name;
			_argc = argc;
			_precedence = precedence;
		}
		private Type(String name, int precedence) {
			this(name, 2, precedence);
		}

		/** Returns the precedence of this operator.
		 * The lower the number, the higher the precedence.
		 */
		public int getPrecedence() {
			return _precedence;
		}
		/** Returns the number arguments that this operator expects.
		 */
		public int getArgumentNumber() {
			return _argc;
		}
		/** Returns the name of this operator.
		 */
		public String getName() {
			return _name;
		}

		/** Invokes the operator.
		 * The number of arguments must match the operator.
		 * For example, for a unary operator, the number shall be 1.
		 */
		public Object invoke(Object... args) {
			throw new UnsupportedOperationException();
		}
		@Override
		public String toString() {
			return '\'' + _name + '\'';
		}
	}
}
