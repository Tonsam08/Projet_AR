package ricmlets.examples;


import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;

public class HelloRicmlet implements httpserver.itf.HttpRicmlet{

	@Override
	public void doGet(HttpRicmletRequest req,  HttpRicmletResponse resp) throws IOException {
		resp.setReplyOk();
		resp.setContentType("text/html");
		PrintStream ps = resp.beginBody();
		ps.println("<HTML><HEAD><TITLE> Ricmlet processing </TITLE></HEAD>");
		ps.print("<BODY><H4> Hello " + req.getArg("name") + " " + req.getArg("surname") + " !!!");
		ps.println("</H4></BODY></HTML>");
		ps.println();
	}
}
