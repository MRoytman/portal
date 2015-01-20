package ch.msf.model.actionrunner;

import ch.msf.error.ConfigException;

public interface ActionRuleRunnerI {
	
	
	/**
	 * verifyConfigParameters allow to verify the validity of params before runtime
	 * @param configParams
	 * @throws ConfigException if configParams are not valid
	 */
	void verifyConfigParameters(String[] configParams) throws ConfigException;
	

	/**
	 * check performs the business check
	 * @param command: the command name to run
	 * @return true if the value comply with the nature of the checker
	 */
	boolean runAction(String command, Object[] runtimeParams);
	
	
	/**
	 * 
	 * @return an error message code
	 */
	Integer getErrorCode();

	void setErrorCode(Integer _ErrorCode);

	String[] getConfigParameters();


}
