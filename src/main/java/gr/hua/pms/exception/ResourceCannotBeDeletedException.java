package gr.hua.pms.exception;

public class ResourceCannotBeDeletedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceCannotBeDeletedException(String msg) {
	    super(msg);
	  }
}
