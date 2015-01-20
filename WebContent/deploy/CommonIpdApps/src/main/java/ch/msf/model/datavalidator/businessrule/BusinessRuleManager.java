package ch.msf.model.datavalidator.businessrule;

import java.io.IOException;

public interface BusinessRuleManager {


	/**
	 * load all business rules configuration
	 * @param resourcePathName
	 * @return true if concept resources have been loaded correctly
	 * @throws IOException
	 */
	public boolean loadBusinessRulesResources(String resourcePathName);


	/**
	 * perform a check on valueToCheck using a checker identified by businessRuleCheckerId
	 * @param businessRuleCheckerId
	 * @param valueToCheck
	 * @param runtimeParams: params for checker
	 * @return null or the first checker that fail
	 */
	BusinessRuleCheckerI check(String businessRuleCheckerId, String valueToCheck, Object[] runtimeParams);

}
