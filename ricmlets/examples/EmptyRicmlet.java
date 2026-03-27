package ricmlets.examples;


import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;

public class EmptyRicmlet implements httpserver.itf.HttpRicmlet{

	@Override
	public void doGet(HttpRicmletRequest req, HttpRicmletResponse resp) throws IOException {
		resp.setReplyError(501, "Not Implemented Method");
		resp.setContentType("text/html");
		PrintStream ps = resp.beginBody();
		ps.println("<HTML><HEAD><TITLE> Ricmlet processing </TITLE></HEAD>");
		ps.println("<BODY><H4> Undefined doGet method in your ricmlet </H4></BODY></HTML>");
	}

}
