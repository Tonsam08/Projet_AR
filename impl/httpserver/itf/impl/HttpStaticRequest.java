package httpserver.itf.impl;

import java.io.IOException;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;

/*
 * This class allows to build an object representing an HTTP static request
 */
public class HttpStaticRequest extends HttpRequest {
	static final String DEFAULT_FILE = "index.html";

	public HttpStaticRequest(HttpServer hs, String method, String ressname) throws IOException {
		super(hs, method, ressname);
	}

	public void process(HttpResponse resp) throws Exception {
		// TO COMPLETE
		String root = super.m_hs.getFolder().getAbsolutePath();
		String requested = super.m_ressname;

		if (requested == null || requested.length() == 0 || requested.equals("/")) {
			requested = "/" + DEFAULT_FILE;
		}

		String path = root + requested;
		java.io.File f = new java.io.File(path);

		if (f.isDirectory()) {
			resp.setReplyError(400, path + " is a directory");
		}

		if (!f.exists() || !f.isFile()) {
			resp.setReplyError(404, "Not Found");
			return;
		}

		if (!f.canRead()) {
			resp.setReplyError(403, "Forbidden");
			return;
		}


		try {
			resp.setReplyOk();
			resp.setContentType(HttpRequest.getContentType(path));
			resp.setContentLength((int) f.length());
			resp.beginBody().write(java.nio.file.Files.readAllBytes(f.toPath()));
		} catch (IOException e) {
			resp.setReplyError(500, "Internal Server Error");
		}

	}
}



