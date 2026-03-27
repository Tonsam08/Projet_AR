package httpserver.itf.impl;

import java.io.IOException;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;

public class UnknownRequest extends HttpRequest {
	public UnknownRequest(HttpServer httpserver, String method, String ressname) throws IOException {
		super(httpserver, method, ressname);
	}

	@Override
	public void process(HttpResponse resp) throws IOException {
		resp.setReplyError(501, "Unknown Method");
	}

}
