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

	public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
		super(hs, method, ressname, br);
		m_args = parseArgs(ressname);
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
		return null;
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
			String value = kv.length > 1 ? decode(kv[1]) : "";
			args.put(key, value);
		}
		return args;
	}

	private static String decode(String value) {
		try {
			return java.net.URLDecoder.decode(value, java.nio.charset.StandardCharsets.UTF_8.name());
		} catch (Exception e) {
			return value;
		}
	}
}
