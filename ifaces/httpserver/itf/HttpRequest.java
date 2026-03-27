package httpserver.itf;

import java.io.IOException;

import httpserver.itf.impl.HttpServer;

/* 
 * This class is used to build an object representing an HTTP request
 */
public abstract class HttpRequest {

	protected HttpServer m_hs; 
	protected String m_method;
	protected String m_ressname;

	public HttpRequest(HttpServer hs, String method, String ressname) throws IOException {
		m_hs = hs;
		m_method = method;
		m_ressname = ressname;
	}

	/*
	 * Return the HTTP method ("GET", "POST", "PUT", ..) of the current request
	 */
	public String getMethod() {
		return m_method;
	}

	/*
	 * Return the resource name (URL) associated to the current request
	 */
	public String getRessname() {
		return m_ressname;
	}

	/*
	 * Return the MIME type of a resource given its name
	 */
	public static String getContentType(String name) {
		if (name.endsWith(".html") || name.endsWith(".htm")) return "text/html";
		else if (name.endsWith(".txt") || name.endsWith(".java")) return "text/plain";
		else if (name.endsWith(".gif") ) return "image/gif";
		else if (name.endsWith(".class") ) return "application/octet-stream";
		else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
		else if (name.endsWith(".pdf"))  return "application/pdf";
		else return "text/plain";
	}
	
	/*
	 * Process the current request, delivering the response on the given HttpResponse object
	 */
	public abstract void process(HttpResponse resp) throws Exception;
	

}
