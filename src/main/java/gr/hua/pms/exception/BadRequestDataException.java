package gr.hua.pms.exception;

public class BadRequestDataException extends RuntimeException {

	  private static final long serialVersionUID = 1L;

	  public BadRequestDataException(String msg) {
	    super(msg);
	  }
}