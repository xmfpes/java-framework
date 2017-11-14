package core.exception;

public class BeanCreationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BeanCreationException() {
		super();
	}

	public BeanCreationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BeanCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanCreationException(String message) {
		super(message);
	}

	public BeanCreationException(Throwable cause) {
		super(cause);
	}

}
