package JavaProject.Backend.exception;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 * HTTP 404 Not Found
 */
public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s를 찾을 수 없습니다. (%s: %s)", resourceName, fieldName, fieldValue));
    }
}
