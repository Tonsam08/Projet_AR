package httpserver.itf;

import java.io.IOException;

import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;


/*
 * Interface provided by an object representing an ricmlet
 */
public interface HttpRicmlet {
	
	/*
	 * This method is called by the server to make the ricmlet handle a GET request. 
	 * Implementation of the doGet method is expected to read the request data, write the response headers, 
	 * get the response's output stream, and finally, write the response data. 
	 * 
	 * @param req: the HttpRicmletRequest object that contains the request the client has made of the ricmlet
	 * @param resp: the HttpRicmletResponse object that contains the response the ricmlet sends to the client
	 * @throws IOException if an input or output error is detected when the ricmlet handles the GET request
	 */
	public void doGet(HttpRicmletRequest req, HttpRicmletResponse resp) throws IOException ;
}
