package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

public class HttpRicmletRequestImpl extends HttpRicmletRequest {
	private Map<String, String> m_args;
	private Map<String, String> m_cookies;

	public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
		super(hs, method, ressname, br);
		m_args = parseArgs(ressname);
		m_cookies = new HashMap<>();
		String ligne_header = br.readLine();
		while (ligne_header != null && ligne_header.length() != 0) {
			int cookie_start = ligne_header.indexOf(":");
			if (cookie_start < 0) {
				ligne_header = br.readLine();
				continue;
			}

			String headerName = ligne_header.substring(0, cookie_start).trim();
			String headerValue = ligne_header.substring(cookie_start + 1).trim();

			if (headerName.equalsIgnoreCase("Cookie")) {
				String[] cookies = headerValue.split(";");

				for (String p : cookies) {
					String[] name_values = p.trim().split("=", 2);
					if (name_values.length == 2)
						m_cookies.put(name_values[0].trim(), decode(name_values[1].trim()));

				}
			}
			ligne_header = br.readLine();
		}
	}

	@Override
	public HttpSession getSession() {

		return new HttpSessionimpl(UUID.randomUUID().toString());
	}

	@Override
	public String getArg(String name) {
		return m_args.get(name);
	}

	@Override
	public String getCookie(String name) {
		return m_cookies.get(name);
	}

	@Override
	public void process(HttpResponse resp) throws Exception {
		if (!(resp instanceof HttpRicmletResponse)) {
			resp.setReplyError(500, "Internal Server Error");
			return;
		}

		String clsname = m_ressname;
		int q = clsname.indexOf('?');
		if (q >= 0) {
			clsname = clsname.substring(0, q);
		}
		if (clsname.startsWith("/")) {
			clsname = clsname.substring(1);
		}
		clsname = clsname.replace('/', '.');
		if (!clsname.startsWith("ricmlets.")) {
			clsname = "ricmlets." + clsname;
		}

		HttpRicmlet ricmlet = m_hs.getInstance(clsname);
		ricmlet.doGet(this, (HttpRicmletResponse) resp);
	}

	private static Map<String, String> parseArgs(String ressname) {
		Map<String, String> args = new HashMap<String, String>();
		int q = ressname.indexOf('?');
		if (q < 0 || q + 1 >= ressname.length()) {
			return args;
		}
		String query = ressname.substring(q + 1);
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			if (pair.length() == 0) {
				continue;
			}
			String[] kv = pair.split("=", 2);
			String key = decode(kv[0]);
			String value;
			if (kv.length > 1) {
				value = decode(kv[1]);
			} else {
				value = "";
			}

			args.put(key, value);
		}
		return args;
	}

	private static String decode(String value) {
		try {
			return java.net.URLDecoder.decode(value, "UTF-8");
		} catch (Exception e) {
			return value;
		}
	}
}
