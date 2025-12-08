package JavaProject.Backend.exception;

/**
 * 잘못된 요청일 때 발생하는 예외
 * HTTP 400 Bad Request
 */
public class InvalidRequestException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public InvalidRequestException(String message) {
        super(message);
    }
    
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
