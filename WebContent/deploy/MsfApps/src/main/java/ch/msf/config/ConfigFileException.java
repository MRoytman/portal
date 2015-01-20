package ch.msf.config;

public class ConfigFileException extends RuntimeException {

	/**
	 * @param message
	 * @param cause
	 */
	public ConfigFileException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ConfigFileException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ConfigFileException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
