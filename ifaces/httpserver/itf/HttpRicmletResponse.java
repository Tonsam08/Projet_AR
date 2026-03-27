package httpserver.itf;

/*
 * Interface provided by an object representing an HTTP response for a dynamic request
 */
public interface HttpRicmletResponse extends HttpResponse {

	/*
	 * Register a cookie in the response object
	 */
	abstract public void setCookie(String name, String value);



}
