package ch.msf.model.datavalidator.businessrule;

import ch.msf.error.ConfigException;

/**
 * IMPLEMENTS BusinessRuleCheckerI WHICH IS A CONCEPT CHECKER CHECKER ARE TAKEN
 * FROM A POOL OF CHECKERS. THIS CLASS IMPOSES THE METHODS TO IMPLEMENT BY USING
 * THE TEMPLATE PATTERN AND TRY TO GUARD AGAINST REENTRANCE PROBLEMS USING
 * SYNCHRONIZED METHODS
 * 
 * @author cmi
 * 
 */
public abstract class AbstractBusinessRuleCheckerBase implements BusinessRuleCheckerI {

	private String _ValueToCheck;
	private String[] _ConfigParameters;
	private Integer _ErrorCode;

	// /**
	// * uniq mandatory constructor
	// * @param args
	// * @param errorCode
	// */
	// public AbstractBusinessRuleCheckerBase(Integer errorCode, String[] args){
	// // define the error code if check fail
	// setErrorCode(errorCode);
	// // first, loaded args should be verified
	// verifyParam(args);
	// }
	//

	/**
	 * verifyParam allow to verify the validity of configuration parameters
	 * before runtime
	 * 
	 * @param args
	 *            : parameters necessary to implement the check
	 * @throws ConfigException
	 *             if configParams are not valid
	 */
	// operations need to be atomic as checkers are taken from a pool
	@Override
	public synchronized void verifyConfigParameters(String[] configParams) throws ConfigException {
		setConfigParameters(configParams);
		verifyConfigParameters();
	}

	/**
	 * perform checking to verify the validity of params before runtime
	 * 
	 * @throws ConfigException
	 *             if params are not valid
	 */
	// Template method pattern
	public abstract void verifyConfigParameters() throws ConfigException;

	/**
	 * 
	 * @param valueToCheck
	 *            : the value to check
	 * @param runtimeParams
	 *            :
	 * @return true if the value comply with the nature of the checker
	 */
	// operations need to be atomic as checkers are taken from a pool
	@Override
	public synchronized boolean check(String valueToCheck, Object[] runtimeParams) {

		setValueToCheck(valueToCheck);
		return check(runtimeParams);
	}

	/**
	 * perform checking based on previously passed parameters
	 * 
	 * @param runtimeParams
	 *            :
	 * @return true if the value comply with the nature of the checker
	 */
	// Template method pattern
	public abstract boolean check(Object[] runtimeParams);

	public String getValueToCheck() {
		return _ValueToCheck;
	}

	public void setValueToCheck(String valueToCheck) {
		this._ValueToCheck = valueToCheck;
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
