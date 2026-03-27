package ricmlets.examples;


import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

public class CountBySessionRicmlet implements httpserver.itf.HttpRicmlet{
	HashMap<String,Integer> counts = new HashMap<String,Integer>();
	
	/*
	 * Print the number of time this ricmlet has been invoked per user session
	 */
	@Override
	public void doGet(HttpRicmletRequest req,  HttpRicmletResponse resp) throws IOException {
		HttpSession s = req.getSession();
		Integer c = (Integer) s.getValue("counter");
		if (c == null)
			s.setValue("counter", new Integer(0));
		else s.setValue("counter", new Integer(c.intValue()+1));
		resp.setReplyOk();
		resp.setContentType("text/html");
		PrintStream ps = resp.beginBody();
		ps.println("<HTML><HEAD><TITLE> Ricmlet processing </TITLE></HEAD>");
		ps.print("<BODY><H4> Hello for the " + s.getValue("counter") + " times !!!");
		ps.println("</H4></BODY></HTML>");
		ps.println();
}

}
