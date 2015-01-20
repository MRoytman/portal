package ch.msf.error;

public class ConfigException extends RuntimeException {

	/**
	 * @param message
	 * @param cause
	 */
	public ConfigException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ConfigException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ConfigException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
