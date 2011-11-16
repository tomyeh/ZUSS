/* Color.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov  9 16:28:08 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss.util;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.zuss.ZussException;

/**
 * Represents a color.
 * @author tomyeh
 */
public class Color implements Comparable<Object> {
	public final int red, green, blue;
	private final String _name;

	/** Returns the color of the given name. */
	public static Color getColor(String rgb) {
		final Color c = getStandardColor(rgb);
		return c != null ? c: new Color(rgb);
	}
	/** Returns the starndard color, or null if it is not a standard color.
	 */
	public static Color getStandardColor(String name) {
		return _colors.get(name.toLowerCase());
	}

	public Color(int value) {
		this(value & 0xff0000, value & 0xff00, value & 0xff);
	}
	public Color(String rgb) {
		final Color c = getStandardColor(rgb);
		if (c != null) {
			red = c.red;
			green = c.green;
			blue = c.blue;
		} else if (rgb.startsWith("#")) {
			if (rgb.length() <= 4) {
				red = decode1(rgb, 1);
				green = decode1(rgb, 2);
				blue = decode1(rgb, 3);
			} else {
				red = decode2(rgb, 1);
				green = decode2(rgb, 3);
				blue = decode2(rgb, 5);
			}
		} else {
			red = green = blue = 255;
		}
		_name = rgb;
	}
	public Color(int red, int green, int blue) {
		this.red = red > 255 ? 255: red < 0 ? 0: red;
		this.green = green > 255 ? 255: green < 0 ? 0: green;
		this.blue = blue > 255 ? 255: blue < 0 ? 0: blue;

		final String s = _names.get(this);
		if (s != null) {
			_name = s;
		} else {
			final StringBuffer sb = new StringBuffer().append('#');
			encode(sb, this.red);
			encode(sb, this.green);
			encode(sb, this.blue);
			_name = sb.toString();
		}
	}

	/** Return a color that negates this color.
	 */
	public Color negate() {
		return new Color(255 - red, 255 - green, 255 - blue);
	}
	/** Returns a color that adds this color and the given object.
	 */
	public Color add(Object o) {
		if (o instanceof Number) {
			o = new Color(((Number)o).intValue());
		} else if (o instanceof String) {
			o = Color.getColor((String)o);
		}
		if (o instanceof Color) {
			Color c = (Color)o;
			return new Color(red + c.red, green + c.green, blue + c.blue);
		}
		if (o == null)
			return this;
		throw new ZussException("Unable to add "+this+" with "+o);
	}
	/** Returns a color that subtracts this color and the given object.
	 */
	public Color subtract(Object o) {
		if (o instanceof Number) {
			o = new Color(((Number)o).intValue());
		} else if (o instanceof String) {
			o = Color.getColor((String)o);
		}
		if (o instanceof Color) {
			Color c = (Color)o;
			return new Color(red - c.red, green - c.green, blue - c.blue);
		}
		if (o == null)
			return this;
		throw new ZussException("Unable to subtract "+this+" with "+o);
	}
	/** Returns a color that multiplies this color with the given object.
	 */
	public Color multiply(Object o) {
		if (o instanceof Number) {
			double v = ((Number)o).doubleValue();
			return new Color((int)Math.round(red * v),
				(int)Math.round(green * v), (int)Math.round(blue * v));
		}
		if (o instanceof Color) {
			Color c = (Color)o;
			return new Color(red * c.red, green * c.green, blue * c.blue);
		}
		if (o == null)
			return this;
		throw new ZussException("Unable to multiply "+this+" with "+o);
	}
	/** Returns a color that divides this color with the given object.
	 */
	public Color divide(Object o) {
		if (o instanceof Number) {
			double v = ((Number)o).doubleValue();
			return new Color((int)Math.round(red / v),
				(int)Math.round(green / v), (int)Math.round(blue / v));
		}
		if (o instanceof Color) {
			Color c = (Color)o;
			return new Color(red / c.red, green / c.green, blue / c.blue);
		}
		throw new ZussException("Unable to divide "+this+" with "+o);
	}
	/** Compares with another object
	 */
	@Override
	public int compareTo(Object o) {
		if (o instanceof Color || o instanceof Number) {
			int v1 = getValue(),
				v2 = o instanceof Number ?
					((Number)o).intValue(): ((Color)o).getValue();
			return v1 > v2 ? 1: v1 == v2 ? 0: -1;
		}
		if (o == null)
			return 1;
		throw new ZussException("Unable to compare "+this+" with "+o);
	}

	/** Returns an integer representing this color
	 */
	public int getValue() {
		return (red << 16) + (green << 8) + blue;
	}

	@Override
	public String toString() {
		return _name;
	}
	@Override
	public int hashCode() {
		return getValue();
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof Color) {
			final Color c = (Color)o;
			return c.red == red && c.green == green && c.blue == blue;
		}
		return false;
	}

	/** Decode single digit. */
	private static int decode1(String rgb, int start) {
		final int v = decode(rgb, start, start + 1);
		return v * 16 + v;
	}
	/** Decode two digits. */
	private static int decode2(String rgb, int start) {
		return decode(rgb, start, start + 2);
	}
	private static int decode(String rgb, int start, int end) {
		try {
			final int len = rgb.length();
			if (start >= len) return 0;
			if (end >= len) end = len;
			final int v = Integer.parseInt(rgb.substring(start, end), 16);
			return v > 255 ? 255: v < 0 ? 0: v;
		} catch (NumberFormatException ex) {
			return 0;
		}
	}
	private static void encode(StringBuffer sb, int c) {
		final String s = Integer.toHexString(c);
		if (s.length() == 1)
			sb.append('0');
		sb.append(s);
	}

	private static Map<String, Color> _colors = new HashMap<String, Color>(196);
	private static Map<Color, String> _names = new HashMap<Color, String>(196);
	static {
		String[] colors = {
			"aliceblue", "#f0f8ff",
			"antiquewhite", "#faebd7",
			"aqua", "#00ffff",
			"aquamarine", "#7fffd4",
			"azure", "#f0ffff",
			"beige", "#f5f5dc",
			"bisque", "#ffe4c4",
			"black", "#000000",
			"blanchedalmond", "#ffebcd",
			"blue", "#0000ff",
			"blueviolet", "#8a2be2",
			"brown", "#a52a2a",
			"burlywood", "#deb887",
			"cadetblue", "#5f9ea0",
			"chartreuse", "#7fff00",
			"chocolate", "#d2691e",
			"coral", "#ff7f50",
			"cornflowerblue", "#6495ed",
			"cornsilk", "#fff8dc",
			"crimson", "#dc143c",
			"cyan", "#00ffff",
			"darkblue", "#00008b",
			"darkcyan", "#008b8b",
			"darkgoldenrod", "#b8860b",
			"darkgray", "#a9a9a9",
			"darkgrey", "#a9a9a9",
			"darkgreen", "#006400",
			"darkkhaki", "#bdb76b",
			"darkmagenta", "#8b008b",
			"darkolivegreen", "#556b2f",
			"darkorange", "#ff8c00",
			"darkorchid", "#9932cc",
			"darkred", "#8b0000",
			"darksalmon", "#e9967a",
			"darkseagreen", "#8fbc8f",
			"darkslateblue", "#483d8b",
			"darkslategray", "#2f4f4f",
			"darkslategrey", "#2f4f4f",
			"darkturquoise", "#00ced1",
			"darkviolet", "#9400d3",
			"deeppink", "#ff1493",
			"deepskyblue", "#00bfff",
			"dimgray", "#696969",
			"dimgrey", "#696969",
			"dodgerblue", "#1e90ff",
			"firebrick", "#b22222",
			"floralwhite", "#fffaf0",
			"forestgreen", "#228b22",
			"fuchsia", "#ff00ff",
			"gainsboro", "#dcdcdc",
			"ghostwhite", "#f8f8ff",
			"gold", "#ffd700",
			"goldenrod", "#daa520",
			"gray", "#808080",
			"grey", "#808080",
			"green", "#008000",
			"greenyellow", "#adff2f",
			"honeydew", "#f0fff0",
			"hotpink", "#ff69b4",
			"indianred ", "#cd5c5c",
			"indigo ", "#4b0082",
			"ivory", "#fffff0",
			"khaki", "#f0e68c",
			"lavender", "#e6e6fa",
			"lavenderblush", "#fff0f5",
			"lawngreen", "#7cfc00",
			"lemonchiffon", "#fffacd",
			"lightblue", "#add8e6",
			"lightcoral", "#f08080",
			"lightcyan", "#e0ffff",
			"lightgoldenrodyellow", "#fafad2",
			"lightgray", "#d3d3d3",
			"lightgrey", "#d3d3d3",
			"lightgreen", "#90ee90",
			"lightpink", "#ffb6c1",
			"lightsalmon", "#ffa07a",
			"lightseagreen", "#20b2aa",
			"lightskyblue", "#87cefa",
			"lightslategray", "#778899",
			"lightslategrey", "#778899",
			"lightsteelblue", "#b0c4de",
			"lightyellow", "#ffffe0",
			"lime", "#00ff00",
			"limegreen", "#32cd32",
			"linen", "#faf0e6",
			"magenta", "#ff00ff",
			"maroon", "#800000",
			"mediumaquamarine", "#66cdaa",
			"mediumblue", "#0000cd",
			"mediumorchid", "#ba55d3",
			"mediumpurple", "#9370d8",
			"mediumseagreen", "#3cb371",
			"mediumslateblue", "#7b68ee",
			"mediumspringgreen", "#00fa9a",
			"mediumturquoise", "#48d1cc",
			"mediumvioletred", "#c71585",
			"midnightblue", "#191970",
			"mintcream", "#f5fffa",
			"mistyrose", "#ffe4e1",
			"moccasin", "#ffe4b5",
			"navajowhite", "#ffdead",
			"navy", "#000080",
			"oldlace", "#fdf5e6",
			"olive", "#808000",
			"olivedrab", "#6b8e23",
			"orange", "#ffa500",
			"orangered", "#ff4500",
			"orchid", "#da70d6",
			"palegoldenrod", "#eee8aa",
			"palegreen", "#98fb98",
			"paleturquoise", "#afeeee",
			"palevioletred", "#d87093",
			"papayawhip", "#ffefd5",
			"peachpuff", "#ffdab9",
			"peru", "#cd853f",
			"pink", "#ffc0cb",
			"plum", "#dda0dd",
			"powderblue", "#b0e0e6",
			"purple", "#800080",
			"red", "#ff0000",
			"rosybrown", "#bc8f8f",
			"royalblue", "#4169e1",
			"saddlebrown", "#8b4513",
			"salmon", "#fa8072",
			"sandybrown", "#f4a460",
			"seagreen", "#2e8b57",
			"seashell", "#fff5ee",
			"sienna", "#a0522d",
			"silver", "#c0c0c0",
			"skyblue", "#87ceeb",
			"slateblue", "#6a5acd",
			"slategray", "#708090",
			"slategrey", "#708090",
			"snow", "#fffafa",
			"springgreen", "#00ff7f",
			"steelblue", "#4682b4",
			"tan", "#d2b48c",
			"teal", "#008080",
			"thistle", "#d8bfd8",
			"tomato", "#ff6347",
			"turquoise", "#40e0d0",
			"violet", "#ee82ee",
			"wheat", "#f5deb3",
			"white", "#ffffff",
			"whitesmoke", "#f5f5f5",
			"yellow", "#ffff00",
			"yellowgreen", "#9acd32"};
		for (int j = 0; j < colors.length; j += 2) {
			_colors.put(colors[j], new Color(colors[j + 1]));
			_names.put(new Color(colors[j + 1]), colors[j]);
		}
	}
}
