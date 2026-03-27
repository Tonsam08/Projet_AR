package httpserver.itf.impl;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpRicmletResponse;

public class HttpRicmletResponseImpl extends HttpResponseImpl implements HttpRicmletResponse {
	private Map<String, String> m_cookies;

	public HttpRicmletResponseImpl(HttpServer hs, HttpRequest req, PrintStream ps) {
		super(hs, req, ps);
		m_cookies = new LinkedHashMap<String, String>();
	}

	@Override
	public void setCookie(String name, String value) {
		m_cookies.put(name, value);
	}

	@Override
	public void setReplyOk() {
		super.setReplyOk();
		for (String key : m_cookies.keySet()) {
			String value = m_cookies.get(key);
			m_ps.println("Set-Cookie: " + key + "=" + value);
		}

	}
}
