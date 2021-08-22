package gr.hua.pms.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ControllerExceptionHandler {
	
	  @ExceptionHandler(ResourceNotFoundException.class)
	  public ResponseEntity<ErrorMessage> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
	    ErrorMessage message = new ErrorMessage(
	        HttpStatus.NOT_FOUND.value(),
	        new Date(),
	        ex.getMessage(),
	        request.getDescription(false));
	    
	    return new ResponseEntity<ErrorMessage>(message, HttpStatus.NOT_FOUND);
	  }

	  @ExceptionHandler(ResourceCannotBeDeletedException.class)
	  public ResponseEntity<ErrorMessage> resourceCannotBeDeletedException(ResourceCannotBeDeletedException ex, WebRequest request) {
	    ErrorMessage message = new ErrorMessage(
	        HttpStatus.FORBIDDEN.value(),
	        new Date(),
	        ex.getMessage(),
	        request.getDescription(false));
	    
	    return new ResponseEntity<ErrorMessage>(message, HttpStatus.FORBIDDEN);
	  }
	  
	  @ExceptionHandler(ResourceAlreadyExistsException.class)
	  public ResponseEntity<ErrorMessage> ResourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
	    ErrorMessage message = new ErrorMessage(
	        HttpStatus.BAD_REQUEST.value(),
	        new Date(),
	        ex.getMessage(),
	        request.getDescription(false));
	    
	    return new ResponseEntity<ErrorMessage>(message, HttpStatus.FORBIDDEN);
	  }
	  
	  /*@ExceptionHandler(Exception.class)
	  public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex, WebRequest request) {
		  System.out.println("Global Exception Handler message "+ex.getMessage());
	    ErrorMessage message = new ErrorMessage(
	        HttpStatus.INTERNAL_SERVER_ERROR.value(),
	        new Date(),
	        ex.getMessage(),
	        request.getDescription(false));
	    
	    return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
	  }*/
	  
	  @ExceptionHandler(UnauthorizedException.class)
	  public ResponseEntity<ErrorMessage> unauthorizedException(UnauthorizedException ex, WebRequest request) {
	    ErrorMessage message = new ErrorMessage(
	        HttpStatus.UNAUTHORIZED.value(),
	        new Date(),
	        ex.getMessage(),
	        request.getDescription(false));
	    
	    return new ResponseEntity<ErrorMessage>(message, HttpStatus.UNAUTHORIZED);
	  }
	  
	  @ExceptionHandler(value = TokenRefreshException.class)
	  @ResponseStatus(HttpStatus.FORBIDDEN)
	  public ErrorMessage handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
	    return new ErrorMessage(
	        HttpStatus.FORBIDDEN.value(),
	        new Date(),
	        ex.getMessage(),
	        request.getDescription(false));
	  }
}