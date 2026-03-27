package ricmlets.examples;


import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;

public class CookieRicmlet implements httpserver.itf.HttpRicmlet{
	boolean f = true;

	@Override
	public void doGet(HttpRicmletRequest req,  HttpRicmletResponse resp) throws IOException {

		String myFirstCookie = req.getCookie("MyFirstCookie");
		if (myFirstCookie == null) 
			resp.setCookie("MyFirstCookie", "1");
		else {
			int n =  Integer.valueOf(myFirstCookie);
				// modify the cookie's value each time the ricmlet is invoked
				resp.setCookie("MyFirstCookie", new Integer(n+1).toString());
		}
	
		resp.setReplyOk();
		resp.setContentType("text/html");
		PrintStream ps = resp.beginBody();
		ps.println("<HTML><HEAD><TITLE> Ricmlet processing </TITLE></HEAD>");
		ps.print("<BODY><H4> MyFirstCookie " + req.getCookie("MyFirstCookie") + "<br>");
		ps.println("</H4></BODY></HTML>");
		ps.println();
}
}
