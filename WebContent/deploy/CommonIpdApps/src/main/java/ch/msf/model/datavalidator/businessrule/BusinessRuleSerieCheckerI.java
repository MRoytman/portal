package ch.msf.model.datavalidator.businessrule;

public interface BusinessRuleSerieCheckerI {

	/**
	 * add a business rule checker to this serie cheker
	 * @param businessRuleCheckerI
	 */
	void addBRChecker(BusinessRuleCheckerI businessRuleCheckerI);

	/**
	 * perform all checks on the checkable
	 * @param valueToCheck 
	 * @return null if all check passed successfully on value or the checker that found an error
	 */
	BusinessRuleCheckerI checkAllBR(String valueToCheck, Object[] runtimeParams);
}
