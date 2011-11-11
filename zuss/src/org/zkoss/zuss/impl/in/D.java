/* D.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov  4 12:19:34 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.impl.in;

import java.util.Collection;

/**
 * Utilities for debugging purpose.
 * @author tomyeh
 */
/*package*/ class D {
	/** Returns the readable message for debugging purpose. */
	/*package*/ static String d(Token token) {
		if (token != null) {
			final String nm = token.getClass().getName();
			return nm.substring(nm.lastIndexOf('.') + 1) + '(' + token + ')';
		}
		return "EOF";
	}
	/** Returns the readable message for debugging purpose. */
	/*package*/ static String d(Collection<? extends Token> col) {
		final StringBuffer sb = new StringBuffer().append('[');
		for (Token t: col) {
			if (sb.length() > 1)
				sb.append(", ");
			sb.append(d(t));
		}
		return sb.append(']').toString();
	}
}
