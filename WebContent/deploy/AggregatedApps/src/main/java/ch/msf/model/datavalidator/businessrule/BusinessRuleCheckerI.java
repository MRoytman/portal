package ch.msf.model.datavalidator.businessrule;

import ch.msf.error.ConfigException;

public interface BusinessRuleCheckerI {
	
	
	/**
	 * verifyConfigParameters allow to verify the validity of params before runtime
	 * @param configParams
	 * @throws ConfigException if configParams are not valid
	 */
	void verifyConfigParameters(String[] configParams) throws ConfigException;
	
	/**
	 * check performs the business check
	 * @param valueToCheck: the value to check
	 * @param runtimeParams:
	 * @return true if the value comply with the nature of the checker
	 */
	boolean check(String valueToCheck, Object[] runtimeParams);
	
	
	/**
	 * 
	 * @return an error message code
	 */
	Integer getErrorCode();

	void setErrorCode(Integer _ErrorCode);

	String[] getConfigParameters();
}
