package ricmlets.examples;


import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;

public class CountRicmlet implements httpserver.itf.HttpRicmlet{
	int count = 0;

	@Override
	public void doGet(HttpRicmletRequest req,  HttpRicmletResponse resp) throws IOException {
		count++;
		resp.setReplyOk();
		resp.setContentType("text/html");
		PrintStream ps = resp.beginBody();
		ps.println("<HTML><HEAD><TITLE> Ricmlet processing </TITLE></HEAD>");
		ps.print("<BODY><H4> Hello for the  " + count + " times !!!");
		ps.println("</H4></BODY></HTML>");
		ps.println();
}
}
