package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpSession;

/**
 * Basic HTTP Server Implementation
 * 
 * Only manages static requests
 * The url for a static ressource is of the form:
 * "http//host:port/<path>/<ressource name>"
 * For example, try accessing the following urls from your brower:
 * http://localhost:<port>/
 * http://localhost:<port>/voile.jpg
 * ...
 */
public class HttpServer {

	private int m_port;
	private File m_folder; // default folder for accessing static resources (files)
	private ServerSocket m_ssoc;
	private Map<String, HttpRicmlet> m_ricmlets = new ConcurrentHashMap<>();
	private Map<String, SessionEntry> m_sessions = new ConcurrentHashMap<>();
	private static final long SESSION_TIMEOUT_MS = 2 * 60 * 1000; // 2 min
	private static final long CLEANUP_PERIOD_MS = 30 * 1000; // 30 s

	private static class SessionEntry {
		final HttpSession session;
		volatile long lastAccess;

		SessionEntry(HttpSession s, long ts) {
			session = s;
			lastAccess = ts;
		}
	}

	protected HttpServer(int port, String folderName) {
		m_port = port;
		if (!folderName.endsWith(File.separator))
			folderName = folderName + File.separator;
		m_folder = new File(folderName);
		try {
			m_ssoc = new ServerSocket(m_port);
			System.out.println("HttpServer started on port " + m_port);
		} catch (IOException e) {
			System.out.println("HttpServer Exception:" + e);
			System.exit(1);
		}
		Thread cleaner = new Thread(() -> {
			while (true) {
				long now = System.currentTimeMillis();
				//m_sessions.entrySet().removeIf(en -> now - en.getValue().lastAccess > SESSION_TIMEOUT_MS);
				synchronized (this){
{}				for (Map.Entry<String, SessionEntry> en : m_sessions.entrySet()) {
					if (now - en.getValue().lastAccess > SESSION_TIMEOUT_MS) {
						m_sessions.remove(en.getKey(), en.getValue());
					}
				}
			}

				try {
					Thread.sleep(CLEANUP_PERIOD_MS);
				} catch (InterruptedException ignored) {
				}
			}
		});
		cleaner.setDaemon(true);
		cleaner.start();

	}

	public File getFolder() {
		return m_folder;
	}

	public synchronized	 HttpRicmlet getInstance(String clsname)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, MalformedURLException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		HttpRicmlet existing = m_ricmlets.get(clsname);
		if (existing != null) {
			return existing;
		}

		Object o = Class.forName(clsname).getDeclaredConstructor().newInstance();
		if (!(o instanceof HttpRicmlet)) {
			throw new IllegalArgumentException("Class " + clsname + " is not a HttpRicmlet");
		}

		HttpRicmlet created = (HttpRicmlet) o;
		HttpRicmlet prev = m_ricmlets.putIfAbsent(clsname, created);
		if (prev != null)
			return prev;
		else
			return created;
	}

	public synchronized HttpSession getSession(String id) {
		if (id == null)
			return null;
		SessionEntry e = m_sessions.get(id);
		if (e == null)
			return null;

		long now = System.currentTimeMillis();
		if (now - e.lastAccess > SESSION_TIMEOUT_MS) {
			m_sessions.remove(id, e); // détruite
			return null;
		}
		e.lastAccess = now;
		return e.session;

	}

	public synchronized HttpSession createSession() {
		long now = System.currentTimeMillis();
		HttpSession s = new HttpSessionimpl(UUID.randomUUID().toString());
		m_sessions.put(s.getId(), new SessionEntry(s, now));
		return s;

	}

	/*
	 * Reads a request on the given input stream and returns the corresponding
	 * HttpRequest object
	 */
	public HttpRequest getRequest(BufferedReader br) throws IOException {
		HttpRequest request = null;

		String startline = br.readLine();
		StringTokenizer parseline = new StringTokenizer(startline);
		String method = parseline.nextToken().toUpperCase();
		String ressname = parseline.nextToken();
		if (method.equals("GET")) {
			String pathOnly = ressname;
			int q = pathOnly.indexOf('?');
			if (q >= 0)
				pathOnly = pathOnly.substring(0, q);
			if (pathOnly.startsWith("/"))
				pathOnly = pathOnly.substring(1);

			if (pathOnly.contains("ricmlets/")) {
				request = new HttpRicmletRequestImpl(this, method, ressname, br);
			} else {
				request = new HttpStaticRequest(this, method, ressname);
			}
		} else
			request = new UnknownRequest(this, method, ressname);
		return request;
	}

	/*
	 * Returns an HttpResponse object associated to the given HttpRequest object
	 */
	public HttpResponse getResponse(HttpRequest req, PrintStream ps) {
		if (req instanceof HttpRicmletRequestImpl) {
			return new HttpRicmletResponseImpl(this, req, ps);
		}
		return new HttpResponseImpl(this, req, ps);
	}

	/*
	 * Server main loop
	 */
	protected void loop() {
		try {
			while (true) {
				Socket soc = m_ssoc.accept();
				(new HttpWorker(this, soc)).start();
			}
		} catch (IOException e) {
			System.out.println("HttpServer Exception, skipping request");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int port = 0;
		if (args.length != 2) {
			System.out.println("Usage: java Server <port-number> <file folder>");
		} else {
			port = Integer.parseInt(args[0]);
			String foldername = args[1];
			HttpServer hs = new HttpServer(port, foldername);
			hs.loop();
		}
	}

}
