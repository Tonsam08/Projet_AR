package httpserver.itf;

import java.io.IOException;
import java.io.PrintStream;


/*
 * Interface provided by an object representing an HTTP response
 */
public interface HttpResponse {

	/*
	 * Write an OK start-line on the response output stream 
	 */
	public void setReplyOk() throws IOException;
	
	/*
	 * Write an ERROR start-line on the response output stream 
	 * @param codeRet  the return code (404, ..)
	 * @param msg      the associated error message
	 */
	public void setReplyError(int codeRet, String msg) throws IOException;
	
	/*
	 * Write the content length of the result on the response output stream 
	 */
	public void setContentLength(int length) throws IOException;
	
	/*
	 * Write the content type of the result on the response output stream 
	 * @param type  the content type ("HTML", "JPG", ..)
	 */
	public void setContentType(String type) throws IOException;
	
	/*
	 * Insert an empty line on the response output stream 
	 */
	public PrintStream beginBody() throws IOException;

}
