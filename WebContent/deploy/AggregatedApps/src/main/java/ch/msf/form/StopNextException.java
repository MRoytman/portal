package ch.msf.form;

public class StopNextException extends RuntimeException {
	
	public Object _DescriptorIdentifier;

	/**
	 * @param message
	 * @param cause
	 */
	public StopNextException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public StopNextException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public StopNextException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
