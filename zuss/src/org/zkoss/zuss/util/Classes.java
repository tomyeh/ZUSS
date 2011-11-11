/* Classes.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov  9 15:23:52 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.util;

import java.util.Date;

/**
 * Class related utilities.
 * @author tomyeh
 */
public class Classes {
	/** Converts an object to the specified class.
	 * It is the same as coerce(cls, val, true).
	 *
	 * @param val the value to convert
	 * @exception ClassCastException if failed to convert
	 */
	public static Object coerce(Class<?> cls, Object val)
	throws ClassCastException {
		if (cls.isInstance(val))
			return val;

		if (String.class == cls) {
			return val != null ? val.toString(): null;
		} else if (Color.class == cls) {
			if (val == null)
				return null;
			return val instanceof Number ? new Color(((Number)val).intValue()):
				val != null ? Color.getColor(val.toString()): null;
		} else if (Size.class == cls) {
			return val != null ? new Size(val.toString()): null;
		} else if (Integer.class == cls || int.class == cls) {
			if (val == null) {
				return Integer.class == cls ? null: 0;
			} else if (val instanceof Integer) { //int.class
				return val;
			} else if (val instanceof Number) {
				return new Integer(((Number)val).intValue());
			} else if (val instanceof String) {
				return new Integer((String)val);
			} else if (val instanceof Color) {
				return ((Color)val).getValue();
			} else if (val instanceof Size) {
				return (int)((Size)val).getValue();
			}
		} else if (Boolean.class == cls || boolean.class == cls) {
			if (val == null) {
				return Boolean.class == cls ? null: Boolean.FALSE;
			} else if (val instanceof Boolean) { //boolean.class
				return val;
			} else if (val instanceof String) {
				return Boolean.valueOf((String)val);
			} else if (val instanceof Number) {
				return Boolean.valueOf(((Number)val).intValue() != 0);
			} else if (val instanceof Color) {
				return ((Color)val).getValue() != 0;
			} else if (val instanceof Size) {
				return ((Size)val).getValue() != 0;
			} else {
				return Boolean.TRUE; //non-null is true
			}
		} else if (Short.class == cls || short.class == cls) {
			if (val == null) {
				return Short.class == cls ? null: 0;
			} else if (val instanceof Short) { //short.class
				return val;
			} else if (val instanceof Number) {
				return new Short(((Number)val).shortValue());
			} else if (val instanceof String) {
				return new Short((String)val);
			} else if (val instanceof Size) {
				return (short)((Size)val).getValue();
			}
		} else if (Long.class == cls || long.class == cls) {
			if (val == null) {
				return Long.class == cls ? null: 0;
			} else if (val instanceof Long) { //long.class
				return val;
			} else if (val instanceof Number) {
				return new Long(((Number)val).longValue());
			} else if (val instanceof String) {
				return new Long((String)val);
			} else if (val instanceof Date) {
				return new Long(((Date)val).getTime());
			} else if (val instanceof Color) {
				return ((Color)val).getValue();
			} else if (val instanceof Size) {
				return (long)((Size)val).getValue();
			}
		} else if (Double.class == cls || double.class == cls) {
			if (val == null) {
				return Double.class == cls ? null: 0;
			} else if (val instanceof Double) { //double.class
				return val;
			} else if (val instanceof Number) {
				return new Double(((Number)val).doubleValue());
			} else if (val instanceof String) {
				return new Double((String)val);
			} else if (val instanceof Date) {
				return new Double(((Date)val).getTime());
			} else if (val instanceof Color) {
				return ((Color)val).getValue();
			} else if (val instanceof Size) {
				return ((Size)val).getValue();
			}
		} else if (Float.class == cls || float.class == cls) {
			if (val == null) {
				return Float.class == cls ? null: 0;
			} else if (val instanceof Float) { //float.class
				return val;
			} else if (val instanceof Number) {
				return new Float(((Number)val).floatValue());
			} else if (val instanceof String) {
				return new Float((String)val);
			} else if (val instanceof Date) {
				return new Float(((Date)val).getTime());
			} else if (val instanceof Color) {
				return ((Color)val).getValue();
			} else if (val instanceof Size) {
				return (float)((Size)val).getValue();
			}
		} else if (Byte.class == cls || byte.class == cls) {
			if (val == null) {
				return Byte.class == cls ? null: 0;
			} else if (val instanceof Byte) { //byte.class
				return val;
			} else if (val instanceof Number) {
				return new Byte(((Number)val).byteValue());
			} else if (val instanceof String) {
				return new Byte((String)val);
			} else if (val instanceof Size) {
				return (byte)((Size)val).getValue();
			}
		} else if (Character.class == cls || char.class == cls) {
			if (val == null) {
				return Character.class == cls ? null: new Character('\u0000');
			} else if (val instanceof Character) { //character.class
				return val;
			} else if (val instanceof Number) {
				return new Character((char)((Number)val).shortValue());
			} else if (val instanceof String) {
				final String s = (String)val;
				return s.length() > 0 ? new Character(s.charAt(0)): new Character('\u0000');
			}
		} else if (Date.class == cls) {
			if (val == null) {
				return null;
			} else if (val instanceof Number) {
				return new Date(((Number)val).longValue());
			}
		} else if (Number.class == cls) {
			if (val == null) {
				return null;
			} else if (val instanceof String) {
				final String s = (String)val;
				return s.indexOf('.') >= 0 || s.indexOf('e') >= 0 ?
					new Double(s): new Integer(s);
			} else if (val instanceof Date) {
				return new Long(((Date)val).getTime());
			} else if (val instanceof Color) {
				return ((Color)val).getValue();
			} else if (val instanceof Size) {
				return ((Size)val).getValue();
			}
		} else {
			if (val == null) {
				return null;
			} else {
				final Class<?>[] argTypes = new Class<?>[1];
				try {
					argTypes[0] = val.getClass();
					return cls.getConstructor(argTypes).newInstance(cls, new Object[] {val});
				} catch (Throwable ex) {
				}
			}
		}

		throw new ClassCastException("Unable to coerce "+val+" to "+cls);
	}
}
