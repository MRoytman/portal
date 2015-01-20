package ch.msf.model.datavalidator;


public interface DataValidator {
	
	boolean doFormatChecks(String valueToCheck);

	Integer checkBusinessRules(String valueToCheck, Object[] runtimeParams);

	Integer runAction(String command, Object[] runtimeParams);
	
	void initParamAction();

}
