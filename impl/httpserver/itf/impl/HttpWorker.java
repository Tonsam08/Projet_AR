package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;

/*
 * Worker class
 * In the current version, a worker thread is created to process one HTTP request
 */
public class HttpWorker extends Thread {
	HttpServer m_hs;  // the reference of the HTTP server
	Socket m_soc; // the socket on which the HHTP request will be received and the response be sent

	HttpWorker(HttpServer hs, Socket soc) {
		m_hs = hs;
		m_soc = soc;
	}
	
	public void run() {
		try {
			// get the input and output streams associated to the socket
			BufferedReader br = new BufferedReader(new InputStreamReader(m_soc.getInputStream()));
			PrintStream ps = new PrintStream(m_soc.getOutputStream());

			// build HttpRequest and HttpResponse objects for these input and output streams
			HttpRequest req = m_hs.getRequest(br);
			HttpResponse resp = m_hs.getResponse(req, ps);
			
			// process the HTTP request
			req.process(resp);
		} catch (Exception e) {
			System.err.println("Server exception, skipping to next request " + e);		
		} finally {
			try {
				m_soc.close();
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e);
			} 
		}
	}

}