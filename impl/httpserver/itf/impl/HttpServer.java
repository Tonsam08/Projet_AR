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
import java.util.concurrent.ConcurrentHashMap;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;


/**
 * Basic HTTP Server Implementation 
 * 
 * Only manages static requests
 * The url for a static ressource is of the form: "http//host:port/<path>/<ressource name>"
 * For example, try accessing the following urls from your brower:
 *    http://localhost:<port>/
 *    http://localhost:<port>/voile.jpg
 *    ...
 */
public class HttpServer {

	private int m_port;
	private File m_folder;  // default folder for accessing static resources (files)
	private ServerSocket m_ssoc;
	final Map<String, HttpRicmlet> m_ricmlets = new ConcurrentHashMap<>();


	protected HttpServer(int port, String folderName) {
		m_port = port;
		if (!folderName.endsWith(File.separator)) 
			folderName = folderName + File.separator;
		m_folder = new File(folderName);
		try {
			m_ssoc=new ServerSocket(m_port);
			System.out.println("HttpServer started on port " + m_port);
		} catch (IOException e) {
			System.out.println("HttpServer Exception:" + e );
			System.exit(1);
		}
	}
	
	public File getFolder() {
		return m_folder;
	}
	
	

	public HttpRicmlet getInstance(String clsname)
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





	/*
	 * Reads a request on the given input stream and returns the corresponding HttpRequest object
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
			if (q >= 0) pathOnly = pathOnly.substring(0, q);
			if (pathOnly.startsWith("/")) pathOnly = pathOnly.substring(1);
			if (pathOnly.endsWith("Ricmlet") || pathOnly.contains("Ricmlet/")) {
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

