package ch.msf.error;

public class FatalException extends RuntimeException {

	/**
	 * @param message
	 * @param cause
	 */
	public FatalException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public FatalException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public FatalException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
