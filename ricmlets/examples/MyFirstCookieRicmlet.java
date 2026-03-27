package ricmlets.examples;

import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;

public class MyFirstCookieRicmlet implements HttpRicmlet {

	@Override
	public void doGet(HttpRicmletRequest req, HttpRicmletResponse resp) throws IOException {
		String cookieValue = req.getCookie("MyFirstCookie");
		int nextValue = 1;

		if (cookieValue != null) {
			try {
				nextValue = Integer.parseInt(cookieValue) + 1;
			} catch (NumberFormatException e) {
				nextValue = 1;
			}
		}

		resp.setCookie("MyFirstCookie", String.valueOf(nextValue));
		resp.setReplyOk();
		resp.setContentType("text/html");

		PrintStream ps = resp.beginBody();
		ps.println("<HTML><HEAD><TITLE>MyFirstCookieRicmlet</TITLE></HEAD>");
		ps.println("<BODY><H4>MyFirstCookie = " + nextValue + "</H4></BODY></HTML>");
	}
}
