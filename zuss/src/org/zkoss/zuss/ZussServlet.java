/* ZussServlet.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov 10 18:49:16 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zuss;

import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.zip.GZIPOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zuss.metainfo.ZussDefinition;

/**
 * The ZUSS servlet used to load ZUSS file and convert it to CSS.
 * <p>The initial parameter allowed (init-param):
 * <dl>
 * <dt>client.cache.hours</dt>
 * <dd>Default: 0<br/>
 * How long the browser is allowed to cache the CSS content.
 * If zero or negative, it means the browser shall not cache it.
 * For a production system, you shall assign a big number, such as 8760,
 * and encode the path with a version number,
 * such as <code>/css/main.zuss?v=a_version_number</code>.
 * </dd>
 * <dt>compress</dt>
 * <dd>Default: true<br/>
 * Whether to compress CSS in GZIP when sending to the browser.</dd>
 * <dt>encoding</dt>
 * <dd>Default: UTF-8<br/>
 * The encoding used to read ZUSS and generate CSS.</dd>
 * <dt>server.cache</dt>
 * <dd>Default: false<br/>
 * Whether to allow this servlet to cache CSS,
 * so ZUSS will be compiled only once for the whole system.
 * Turn it on for the production system.</dd>
 * </dl>
 * @author tomyeh
 */
public class ZussServlet extends HttpServlet {
	private static long LAST_MODIFIED = new java.util.Date().getTime();

	private final Map<String, byte[]> _cssmap = new HashMap<String, byte[]>();
	private String _encoding;
	private int _clientCacheHours;
	private boolean _serverCache, _compress;

	@Override
	public void init() throws ServletException {
		final ServletConfig config = getServletConfig();
		_encoding = config.getInitParameter("encoding");
		if (_encoding == null || _encoding.length() == 0)
			_encoding = "UTF-8";
		String s = config.getInitParameter("client.cache.hours");
		if (s != null) {
			try {
				_clientCacheHours = Integer.parseInt(s, 10);
			} catch (Throwable t) {
				log("The initial parameter called client.cache.hours is wrong", t);
			}
		}

		_serverCache = test(config, "server.cache", false);
		_compress = test(config, "compress", true);
	}
	private static boolean test(ServletConfig config, String param, boolean defValue) {
		final String s = config.getInitParameter(param);
		return s != null ? "true".equals(s): defValue;
	}

	@Override
	protected
	void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		final String path = getPath(request);
		try {
			byte[] css = translate(path);
			if (_compress && css.length > 200 && !isIncluded(request))
				css = gzip(request, response, css);

			setCacheControl(response);
			response.setContentType("text/css;charset=" + _encoding);
			response.setContentLength(css.length);
			final OutputStream os = response.getOutputStream();
			os.write(css);
			response.flushBuffer();
		} catch (ZussException ex) {
			log("Failed to translate "+path, ex);
			throw ex;
		}	
	}

	/** Translate ZUSS to CSS.
	 * @param path the path of the ZUSS file, which is decided by {@link #getPath}.
	 * @return the CSS content in a byte array (it is not compressed).
	 */
	protected byte[] translate(String path) throws IOException {
		if (_serverCache) {
			final byte[] css = _cssmap.get(path);
			if (css != null)
				return css;
		}

		int j = path.lastIndexOf('/');
		final ZussDefinition def = Zuss.parse(getInputStream(path), _encoding,
			new ServletContextLocator(getServletContext(),
				j > 0 ? path.substring(0, j): "/", _encoding));

		final StringWriter out = new StringWriter();
		Zuss.translate(def, out, getResolver());
		final byte[] css = out.toString().getBytes(_encoding);

		if (_serverCache)
			_cssmap.put(path, css);
		return css;
	}
	/** Returns a resolver used to provide the default variables and functions.
	 * <p>Default: null (no  default variables and functions).
	 */
	protected Resolver getResolver() {
		return null;
	}
	/** Returns the input stream of the given path.
	 * <p>Default: invoke getServletContext().getResourceAsStream() to load the ZUSS file.
	 * @param path the path of the ZUSS file, which is decided by {@link #getPath}.
	 */
	protected InputStream getInputStream(String path) {
		return getServletContext().getResourceAsStream(path);
	}
	/** Retrieves the path of the ZUSS file from the request.
	 * <p>Default: returns request.getPath()
	 */
	protected String getPath(HttpServletRequest request) {
		String path = (String)request.getAttribute("javax.servlet.include.servlet_path");
		return path != null ? path: request.getServletPath();
	}
	/** Returns if this request is caused by being included by another servlet.
	 */
	protected boolean isIncluded(HttpServletRequest request) {
		return request.getAttribute("javax.servlet.include.context_path") != null;
	}
	private static final byte[] gzip(HttpServletRequest request,
	HttpServletResponse response, byte[] data) throws IOException {
		//We check Content-Encoding first to avoid compressing twice
		String ae = request.getHeader("accept-encoding");
		if (ae != null && !response.containsHeader("Content-Encoding")) {
			if (ae.indexOf("gzip") >= 0) {
				response.addHeader("Content-Encoding", "gzip");
				final ByteArrayOutputStream boas = new ByteArrayOutputStream(8192);
				final GZIPOutputStream gzs = new GZIPOutputStream(boas);
				gzs.write(data);
				gzs.finish();
				return boas.toByteArray();
			}
		}
		return data;
	}
	/** Sets the cache-control related headers.
	 * <p>Default: it depends on client.cache.hours
	 */
	protected void setCacheControl(HttpServletResponse response) {
		if (_clientCacheHours > 0) {
			response.setHeader("Cache-Control", "public, max-age="
				+ _clientCacheHours * 3600); //unit: seconds

			final Calendar cal = Calendar.getInstance();
			cal.add(cal.HOUR, _clientCacheHours);
			response.setDateHeader("Expires", cal.getTime().getTime());
			response.setDateHeader("Last-Modified", LAST_MODIFIED);
		}
	}
}
