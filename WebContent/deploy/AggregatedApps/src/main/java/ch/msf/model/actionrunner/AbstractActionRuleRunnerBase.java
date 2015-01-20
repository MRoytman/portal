package ch.msf.model.actionrunner;

import ch.msf.error.ConfigException;

/**
 * IMPLEMENTS ActionRuleRunnerI WHICH IS A CONCEPT CHECKER CHECKER ARE TAKEN
 * FROM A POOL OF CHECKERS. THIS CLASS IMPOSES THE METHODS TO IMPLEMENT BY USING
 * THE TEMPLATE PATTERN AND TRY TO GUARD AGAINST REENTRANCE PROBLEMS USING
 * SYNCHRONIZED METHODS
 * 
 * @author cmi
 * 
 */
public abstract class AbstractActionRuleRunnerBase implements ActionRuleRunnerI{

	private String _Command;
	private String[] _ConfigParameters;
	private Integer _ErrorCode;

	/**
	 * verifyParam allow to verify the validity of configuration parameters before runtime
	 * @param args: parameters necessary to implement the check
	 * @throws ConfigException if configParams are not valid
	 */
	// operations need to be atomic as checkers are taken from a pool
	@Override
	public synchronized void verifyConfigParameters(String[] configParams) throws ConfigException {
		setConfigParameters(configParams);
		verifyConfigParameters();
	}
	

	/**
	 * perform checking to verify the validity of params before runtime
	 * @throws ConfigException if params are not valid
	 */
	// Template method pattern
	public abstract void verifyConfigParameters() throws ConfigException;
	
	


	/**
	 * check performs the business check
	 * @param command: the command name to run
	 * @return true if the value comply with the nature of the checker
	 */
	@Override
	public abstract boolean runAction(String command, Object[] runtimeParams);
	
	
	public String getCommand() {
		return _Command;
	}

	public void setCommand(String valueToCheck) {
		this._Command = valueToCheck;
	}
	
	@Override
	public String[] getConfigParameters() {
		return _ConfigParameters;
	}

	public void setConfigParameters(String[] args) {
		this._ConfigParameters = args;
	}
	
	/**
	 * 
	 * @return an error message code
	 */
	@Override
	public Integer getErrorCode() {
		return _ErrorCode;
	}

	@Override
	public void setErrorCode(Integer errorCode) {
		this._ErrorCode = errorCode;
	}


}
