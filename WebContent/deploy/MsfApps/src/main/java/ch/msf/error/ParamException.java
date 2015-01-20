package ch.msf.error;

/**
 * return to the user when param problem
 * @author cmi
 *
 */

public class ParamException extends Exception {

	/**
	 * @param message
	 * @param cause
	 */
	public ParamException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ParamException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ParamException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
