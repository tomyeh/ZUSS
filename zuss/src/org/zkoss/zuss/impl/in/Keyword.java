/* Keyword.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov  4 11:04:11 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.in;

/**
 * Represents a keyword, such as @if, @elif and @include.
 * @author tomyeh
 */
/*package*/ class Keyword implements Token {
	private final Value _value;
	private final int _lineno;

	public Keyword(Value value, int lineno) {
		_value = value;
		_lineno = lineno;
	}
	/** Returns the value.
	 */
	public Value getValue() {
		return _value;
	}

	@Override
	public int getLine() {
		return _lineno;
	}

	@Override
	public String toString() {
		return _value.value;
	}
	/** The value of a keyword. */
	/*package*/ static enum Value {
		IF("@if"), ELSE("@else"), ELIF("@elif"), INCLUDE("@include"),
		/** CSS's @import. */
		IMPORT("@import"),
		/** CSS's @charset. */
		CHARSET("@charset"),
		/** CSS's @media. */
		MEDIA("@media");

		private final String value;
		private Value(String value) {
			this.value = value;
		}
	};
}
