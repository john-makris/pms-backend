package gr.hua.pms.exception;

public class WrongFileTypeException extends RuntimeException {

	  private static final long serialVersionUID = 1L;

	  public WrongFileTypeException(String msg) {
	    super(msg);
	  }
}